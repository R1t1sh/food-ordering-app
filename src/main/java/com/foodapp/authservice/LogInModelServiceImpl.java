package com.foodapp.authservice;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodapp.authexceptions.AuthorizationException;
import com.foodapp.authmodels.LogInModel;
import com.foodapp.authmodels.SignUpModel;
import com.foodapp.authmodels.UserSession;
import com.foodapp.authrepository.SignUpModelDAO;
import com.foodapp.authrepository.UserSessionDAO;

@Service
public class LogInModelServiceImpl implements LogInModelService {

	@Autowired
	private SignUpModelDAO signUpDAO;

	@Autowired
	private UserSessionDAO userSessionDAO;

	@Override
	public String LogIn(LogInModel loginData) throws AuthorizationException {
		Optional<SignUpModel> opt = signUpDAO.findByUserName(loginData.getUserName());

		if (!opt.isPresent()) {
			throw new AuthorizationException("Invalid Username");
		}

		SignUpModel user = opt.get();

		if (!user.getPassword().equals(loginData.getPassword())) {
			throw new AuthorizationException("Incorrect Password.");
		}

		Optional<UserSession> currentUserOptional = userSessionDAO.findByUserId(user.getUserId());
		if (currentUserOptional.isPresent()) {
			throw new AuthorizationException("User Already Logged In");
		}

		String key = RandomString.getRandomString();
		String role = user.getRole();
		if (role == null) {
			role = "USER"; //  Default role is USER
			user.setRole(role);
			signUpDAO.save(user);
		}

		UserSession currentUserSession = new UserSession(user.getUserId(), key, LocalDateTime.now(),role);
		userSessionDAO.save(currentUserSession);

		return key; //  Return session key
	}

	@Override
	public String LogOut(String key) throws AuthorizationException {
		Optional<UserSession> currentUserOptional = userSessionDAO.findByUUID(key);
		if (!currentUserOptional.isPresent()) {
			throw new AuthorizationException("Invalid credentials...");
		}

		UserSession userSession = currentUserOptional.get();
		userSessionDAO.delete(userSession);
		return "Logged Out Successfully!";
	}
}
