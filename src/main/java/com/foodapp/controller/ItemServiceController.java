package com.foodapp.controller;

import java.util.List;

import com.foodapp.authexceptions.AuthorizationException;
import com.foodapp.authservice.UserSessionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.foodapp.exceptions.ItemException;
import com.foodapp.model.Item;
import com.foodapp.service.ItemService;


@RestController
@RequestMapping("/item")
public class ItemServiceController {
	
	@Autowired
	ItemService itemService;


	@Autowired
	private UserSessionServiceImpl userSessionService;
	
	
	@PostMapping("/add")
	public ResponseEntity<Item> addItem(@RequestBody Item item, @RequestParam String key) throws ItemException, AuthorizationException {


		String role = userSessionService.getUserRole(key);
		if (!"ADMIN".equals(role) && !"MANAGER".equals(role)) {
			throw new AuthorizationException("Access Denied: Only ADMIN or MANAGER can add items.");
		}

		Item newItem = itemService.addItem(item);
		return new ResponseEntity<Item>(newItem, HttpStatus.CREATED);
	}
	
	@PutMapping("/update")
	public ResponseEntity<Item> updateItem(@RequestBody Item item,@RequestParam String key) throws ItemException,AuthorizationException{


		String role = userSessionService.getUserRole(key);
		if (!"ADMIN".equals(role) && !"MANAGER".equals(role)) {
			throw new AuthorizationException("Access Denied: Only ADMIN or MANAGER can update items.");
		}

		Item updatedItem = itemService.updateItem(item);
		return new ResponseEntity<Item>(updatedItem, HttpStatus.OK);
	}
	
	@GetMapping("/view/{itemId}")
	public ResponseEntity<Item> getItem(@PathVariable("itemId") Integer itemId) throws ItemException{


		Item item = itemService.viewItem(itemId);
		return new ResponseEntity<Item>(item, HttpStatus.ACCEPTED);
	}
	
	@DeleteMapping("/remove/{itemId}")
	public ResponseEntity<Item> removeItem(@PathVariable("itemId") Integer itemId,@RequestParam String key) throws ItemException,AuthorizationException{
		String role = userSessionService.getUserRole(key);
		if (!"ADMIN".equals(role) && !"MANAGER".equals(role)) {
			throw new AuthorizationException("Access Denied: Only ADMIN or MANAGER can remove items.");
		}
		Item removedItem = itemService.removeItem(itemId);
		return new ResponseEntity<Item>(removedItem, HttpStatus.ACCEPTED);
	}

	
	@GetMapping("/viewall")
	public ResponseEntity<List<Item>> getAllItems() throws ItemException{
		List<Item> items = itemService.viewAllItems();
		return new ResponseEntity<List<Item>>(items, HttpStatus.OK);
		
	}
}
