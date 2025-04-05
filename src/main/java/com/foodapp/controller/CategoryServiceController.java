package com.foodapp.controller;

import java.util.List;
import javax.validation.Valid;

import com.foodapp.authexceptions.AuthorizationException;
import com.foodapp.authservice.UserSessionService;
import com.foodapp.exceptions.CategoryException;
import com.foodapp.model.Category;
import com.foodapp.service.CategoryService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryServiceController {

	@Autowired
	private CategoryService catService;

	@Autowired
	private UserSessionService userSessionService;

	@PostMapping("/add")
	public ResponseEntity<Category> addCategory(@Valid @RequestBody Category category, @RequestParam String key) throws CategoryException, AuthorizationException {
		log.info("Attempting to add new category");
		String role = userSessionService.getUserRole(key);
		if (!"ADMIN".equals(role) && !"MANAGER".equals(role)) {
			log.warn("Unauthorized access attempt by role: {}", role);
			throw new AuthorizationException("Access Denied: Only ADMIN and MANAGER can add categories.");
		}

		Category newCategory = catService.addCategory(category);
		log.info("Category added successfully with ID: {}", newCategory.getCategoryId());
		return new ResponseEntity<>(newCategory, HttpStatus.CREATED);
	}

	@PutMapping("/update")
	public ResponseEntity<Category> updateCategory(@RequestBody Category category, @RequestParam String key) throws CategoryException, AuthorizationException {
		log.info("Attempting to update category ID: {}", category.getCategoryId());

		String role = userSessionService.getUserRole(key);
		if (!"ADMIN".equals(role) && !"MANAGER".equals(role)) {
			log.warn("Unauthorized update attempt by role: {}", role);
			throw new AuthorizationException("Access Denied: Only ADMIN and MANAGER can update categories.");
		}

		Category updatedCategory = catService.updateCategory(category);
		log.info("Category updated successfully");
		return new ResponseEntity<>(updatedCategory, HttpStatus.ACCEPTED);
	}

	@GetMapping("/view/{categoryId}")
	public ResponseEntity<Category> getCategory(@PathVariable Integer categoryId, @RequestParam String key) throws CategoryException, AuthorizationException {
		log.info("Fetching category ID: {}", categoryId);

		Integer sessionId = userSessionService.getUserSessionId(key);
		if (sessionId == null) {
			log.warn("Invalid session key used");
			throw new AuthorizationException("Unauthorized: Invalid session key.");
		}

		Category category = catService.viewCategory(categoryId);
		log.debug("Category details: {}", category);
		return new ResponseEntity<>(category, HttpStatus.OK);
	}

	@DeleteMapping("/remove/{categoryId}")
	public ResponseEntity<Category> removeCategory(@PathVariable Integer categoryId, @RequestParam String key) throws CategoryException, AuthorizationException {
		log.info("Attempting to delete category ID: {}", categoryId);

		String role = userSessionService.getUserRole(key);
		if (!"ADMIN".equals(role) && !"MANAGER".equals(role)) {
			log.warn("Unauthorized delete attempt by role: {}", role);
			throw new AuthorizationException("Access Denied: Only ADMIN and MANAGER can remove categories.");
		}

		Category removedCategory = catService.removeCategory(categoryId);
		log.info("Category removed successfully");
		return new ResponseEntity<>(removedCategory, HttpStatus.OK);
	}

	@GetMapping("/viewall")
	public ResponseEntity<List<Category>> getAllCategories() throws CategoryException {
		log.info("Fetching all categories");

		List<Category> categories = catService.viewAllCategory();
		log.debug("Found {} categories", categories.size());
		return new ResponseEntity<>(categories, HttpStatus.OK);
	}
}
