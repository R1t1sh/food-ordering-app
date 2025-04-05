package com.foodapp.controller;

import java.util.List;

import com.foodapp.authexceptions.AuthorizationException;
import com.foodapp.authservice.UserSessionServiceImpl;
import com.foodapp.exceptions.ItemException;
import com.foodapp.model.Item;
import com.foodapp.service.ItemService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/item")
@Slf4j
public class ItemServiceController {

	@Autowired
	private ItemService itemService;

	@Autowired
	private UserSessionServiceImpl userSessionService;

	@PostMapping("/add")
	public ResponseEntity<Item> addItem(@RequestBody Item item, @RequestParam String key) throws ItemException, AuthorizationException {
		log.info("Attempting to add item");
		String role = userSessionService.getUserRole(key);
		if (!"ADMIN".equals(role) && !"MANAGER".equals(role)) {
			log.warn("Unauthorized add attempt by role: {}", role);
			throw new AuthorizationException("Access Denied: Only ADMIN or MANAGER can add items.");
		}
		Item newItem = itemService.addItem(item);
		log.info("Item added with ID: {}", newItem.getItemId());
		return new ResponseEntity<>(newItem, HttpStatus.CREATED);
	}

	@PutMapping("/update")
	public ResponseEntity<Item> updateItem(@RequestBody Item item, @RequestParam String key) throws ItemException, AuthorizationException {
		log.info("Attempting to update item with ID: {}", item.getItemId());
		String role = userSessionService.getUserRole(key);
		if (!"ADMIN".equals(role) && !"MANAGER".equals(role)) {
			log.warn("Unauthorized update attempt by role: {}", role);
			throw new AuthorizationException("Access Denied: Only ADMIN or MANAGER can update items.");
		}
		Item updatedItem = itemService.updateItem(item);
		log.info("Item updated successfully");
		return new ResponseEntity<>(updatedItem, HttpStatus.OK);
	}

	@GetMapping("/view/{itemId}")
	public ResponseEntity<Item> getItem(@PathVariable Integer itemId) throws ItemException {
		log.info("Fetching item with ID: {}", itemId);
		Item item = itemService.viewItem(itemId);
		return new ResponseEntity<>(item, HttpStatus.ACCEPTED);
	}

	@DeleteMapping("/remove/{itemId}")
	public ResponseEntity<Item> removeItem(@PathVariable Integer itemId, @RequestParam String key) throws ItemException, AuthorizationException {
		log.info("Removing item with ID: {}", itemId);
		String role = userSessionService.getUserRole(key);
		if (!"ADMIN".equals(role) && !"MANAGER".equals(role)) {
			log.warn("Unauthorized delete attempt by role: {}", role);
			throw new AuthorizationException("Access Denied: Only ADMIN or MANAGER can remove items.");
		}
		Item removedItem = itemService.removeItem(itemId);
		return new ResponseEntity<>(removedItem, HttpStatus.ACCEPTED);
	}

	@GetMapping("/viewall")
	public ResponseEntity<List<Item>> getAllItems() throws ItemException {
		log.info("Fetching all items");
		List<Item> items = itemService.viewAllItems();
		log.debug("Found {} items", items.size());
		return new ResponseEntity<>(items, HttpStatus.OK);
	}
}
