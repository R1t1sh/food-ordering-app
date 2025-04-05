package com.foodapp.controller;

import java.util.List;

import com.foodapp.authexceptions.AuthorizationException;
import com.foodapp.authservice.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.foodapp.exceptions.CategoryException;
import com.foodapp.model.Category;
import com.foodapp.service.CategoryService;


import javax.validation.Valid;

@RestController
@RequestMapping("/category")
public class CategoryServiceController {
	
	@Autowired
	CategoryService catService;

	@Autowired
	private UserSessionService userSessionService;
	
	
	@PostMapping("/add")
	public ResponseEntity<Category> addCategory(@Valid @RequestBody Category category, @RequestParam String key) throws CategoryException, AuthorizationException {
		String role = userSessionService.getUserRole(key);

		if (!"ADMIN".equals(role) && !"MANAGER".equals(role)) {
			throw new AuthorizationException("Access Denied: Only ADMIN and MANAGER can add categories.");
		}
		Category newCategory = catService.addCategory(category);
		return new ResponseEntity<Category>(newCategory, HttpStatus.CREATED); 
	}
	
	@PutMapping("/update")
	public ResponseEntity<Category> updateCategory(@RequestBody Category category,@RequestParam String key) throws CategoryException,AuthorizationException{


		String role = userSessionService.getUserRole(key);

		if (!"ADMIN".equals(role) && !"MANAGER".equals(role)) {
			throw new AuthorizationException("Access Denied: Only ADMIN and MANAGER can update categories.");
		}
		Category updatedCategory = catService.updateCategory(category);
		return new ResponseEntity<Category>(updatedCategory, HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/view/{categoryId}")
	public ResponseEntity<Category> getCategory(@PathVariable("categoryId") Integer categoryId,@RequestParam String key) throws CategoryException,AuthorizationException{

		Integer sessionId = userSessionService.getUserSessionId(key);
		if (sessionId == null) {
			throw new AuthorizationException("Unauthorized: Invalid session key.");
		}
		Category category = catService.viewCategory(categoryId);
		return new ResponseEntity<Category>(category, HttpStatus.OK);
	}
	
	
	@DeleteMapping("/remove/{categoryId}")
	public ResponseEntity<Category> removeCategory(@PathVariable("categoryId") Integer categoryId,@RequestParam String key) throws CategoryException,AuthorizationException{


		String role = userSessionService.getUserRole(key);

		if (!"ADMIN".equals(role) && !"MANAGER".equals(role)) {
			throw new AuthorizationException("Access Denied: Only ADMIN and MANAGER can remove categories.");
		}

		Category removedCategory = catService.removeCategory(categoryId);
		return new ResponseEntity<Category>(removedCategory, HttpStatus.OK);
	}
	
	
	@GetMapping("/viewall")
	public ResponseEntity<List<Category>> getAllCategories() throws CategoryException{

		List<Category> categories = catService.viewAllCategory();
		return new ResponseEntity<List<Category>>(categories, HttpStatus.OK);
	}
	

}
