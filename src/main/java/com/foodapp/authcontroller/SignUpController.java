package com.foodapp.authcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import com.foodapp.authexceptions.AuthorizationException;
import com.foodapp.authmodels.SignUpModel;
import com.foodapp.authservice.SignUpModelService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class SignUpController {

	@Autowired
	private SignUpModelService signUpService;


	@PostMapping("/signUp")
	public ResponseEntity<?> createNewSignUpHandler(@RequestBody SignUpModel newSignUp) {
		try {
			SignUpModel newSignedUp = signUpService.newSignUp(newSignUp);

			//  response with a success message
			Map<String, Object> response = new HashMap<>();
			response.put("message", "User Registered Successfully");
			response.put("user", newSignedUp); // Optionally include user details

			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (AuthorizationException e) {
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("error", e.getMessage());

			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
	}


	@PutMapping("/updateSignUp")
	public ResponseEntity<SignUpModel> updateSignUpDetailsHandler(@RequestBody SignUpModel signUp, @RequestParam String key) throws AuthorizationException
	{
		SignUpModel newUpdatedSignUp = signUpService.updateSignUp(signUp,key);

		return new ResponseEntity<SignUpModel>(newUpdatedSignUp,HttpStatus.ACCEPTED);


	}

}
