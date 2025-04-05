package com.foodapp.controller;

import javax.validation.Valid;

import com.foodapp.authexceptions.AuthorizationException;
import com.foodapp.authservice.UserSessionService;
import com.foodapp.exceptions.RestaurantException;
import com.foodapp.model.Restaurant;
import com.foodapp.service.RestaurantService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/restaurant")
@Slf4j
public class RestaurantServiceController {

	@Autowired
	private RestaurantService restService;

	@Autowired
	private UserSessionService userSessionService;

	@PostMapping("/add")
	public ResponseEntity<Restaurant> saveResturant(@Valid @RequestBody Restaurant restaurant, @RequestParam String key)
			throws RestaurantException, AuthorizationException {
		log.info("Adding new restaurant: {}", restaurant.getRestaurantName());
		String role = userSessionService.getUserRole(key);
		if (!"ADMIN".equals(role)) {
			log.warn("Unauthorized add attempt by role: {}", role);
			throw new AuthorizationException("Access Denied: Only ADMIN can add restaurants.");
		}
		Restaurant newRestaurant = restService.addRestaurant(restaurant);
		return new ResponseEntity<>(newRestaurant, HttpStatus.CREATED);
	}

	@PutMapping("/update")
	public ResponseEntity<Restaurant> updateResturant(@Valid @RequestBody Restaurant restaurant, @RequestParam String key)
			throws RestaurantException, AuthorizationException {
		log.info("Updating restaurant ID: {}", restaurant.getRestaurantId());
		String role = userSessionService.getUserRole(key);
		if (!"ADMIN".equals(role)) {
			log.warn("Unauthorized update attempt by role: {}", role);
			throw new AuthorizationException("Access Denied: Only ADMIN can update restaurants.");
		}
		Restaurant updatedResturant = restService.updateRestaurant(restaurant);
		return new ResponseEntity<>(updatedResturant, HttpStatus.ACCEPTED);
	}

	@DeleteMapping("/remove/{restaurantId}")
	public ResponseEntity<Restaurant> deleteRestaurant(@PathVariable Integer restaurantId, @RequestParam String key)
			throws RestaurantException, AuthorizationException {
		log.info("Deleting restaurant ID: {}", restaurantId);
		String role = userSessionService.getUserRole(key);
		if (!"ADMIN".equals(role)) {
			log.warn("Unauthorized delete attempt by role: {}", role);
			throw new AuthorizationException("Access Denied: Only ADMIN can remove restaurants.");
		}
		Restaurant removedRestaurant = restService.removeRestaurant(restaurantId);
		return new ResponseEntity<>(removedRestaurant, HttpStatus.OK);
	}

	@GetMapping("/view/{restaurantId}")
	public ResponseEntity<Restaurant> getByResturantId(@PathVariable Integer restaurantId, @RequestParam String key)
			throws RestaurantException, AuthorizationException {
		log.info("Fetching restaurant ID: {}", restaurantId);
		Integer sessionId = userSessionService.getUserSessionId(key);
		if (sessionId != null) {
			Restaurant restaurant = restService.viewRestaurant(restaurantId);
			return new ResponseEntity<>(restaurant, HttpStatus.ACCEPTED);
		} else {
			log.error("Invalid session key: {}", key);
			throw new RestaurantException("Invalid session key.");
		}
	}
}
