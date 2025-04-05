//package com.foodapp.authcontroller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PatchMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.foodapp.authexceptions.AuthorizationException;
//import com.foodapp.authmodels.LogInModel;
//import com.foodapp.authservice.LogInModelServiceImpl;
//
//@RestController
//public class LoginController {
//
//	@Autowired
//	private LogInModelServiceImpl loginService;
//
//	@PostMapping("/login")
//	public String loginHandler(@RequestBody LogInModel loginData) throws AuthorizationException {
//		return loginService.LogIn(loginData);
//	}
//
//	@PatchMapping("/logout")
//	public String logOutFromAccount(@RequestParam String key) throws AuthorizationException
//	{
//		return loginService.LogOut(key);
//	}
//
//
//}
package com.foodapp.authcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.foodapp.authexceptions.AuthorizationException;
import com.foodapp.authmodels.LogInModel;
import com.foodapp.authservice.LogInModelServiceImpl;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.foodapp.authexceptions.AuthorizationException;
import com.foodapp.authmodels.LogInModel;
import com.foodapp.authservice.LogInModelServiceImpl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import com.foodapp.authexceptions.AuthorizationException;
import com.foodapp.authmodels.LogInModel;
import com.foodapp.authservice.LogInModelServiceImpl;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*") // Allow frontend access
public class LoginController {

	@Autowired
	private LogInModelServiceImpl loginService;

	// ✅ Login API (Returns JSON)
	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> loginHandler(@RequestBody LogInModel loginData) {
		Map<String, String> response = new HashMap<>();
		try {
			String sessionKey = loginService.LogIn(loginData);
			response.put("message", "Login Successful");
			response.put("sessionKey", sessionKey);
			return ResponseEntity.ok(response); // ✅ Return JSON response
		} catch (AuthorizationException e) {
			response.put("message", e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
	}

	// ✅ Logout API (Returns JSON)
	@PostMapping("/logout")
	public ResponseEntity<Map<String, String>> logOutFromAccount(@RequestBody Map<String, String> requestBody) {
		Map<String, String> response = new HashMap<>();

		String key = requestBody.get("sessionKey"); // Extract session key from JSON request body

		if (key == null || key.isEmpty()) {
			response.put("message", "Session key is missing");
			return ResponseEntity.badRequest().body(response);
		}

		try {
			String message = loginService.LogOut(key);
			response.put("message", message);
			return ResponseEntity.ok(response);
		} catch (AuthorizationException e) {
			response.put("message", e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
	}

}
