package com.foodapp.authservice;

import com.foodapp.authexceptions.AuthorizationException;
import com.foodapp.authmodels.SignUpModel;
import com.foodapp.authmodels.UserSession;

public interface UserSessionService {

	 UserSession getUserSession(String key) throws AuthorizationException;

	 Integer getUserSessionId(String key) throws AuthorizationException;

	 SignUpModel getSignUpDetails(String key) throws AuthorizationException;
	 String getUserRole(String key) throws AuthorizationException;

}
