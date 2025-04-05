package com.foodapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foodapp.authexceptions.AuthorizationException;
import com.foodapp.authservice.UserSessionService;
import com.foodapp.exceptions.CartException;
import com.foodapp.exceptions.ItemException;
import com.foodapp.model.FoodCart;
import com.foodapp.service.FoodCartService;

import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/cart")
public class FoodCartServiceController {

	@Autowired
	FoodCartService cartService;

	@Autowired
	UserSessionService userSessionService;

	// Only Employees (including Restaurant Managers) can register food carts
	@PostMapping("/register")
	public ResponseEntity<FoodCart> saveCartDetails(@RequestBody FoodCart fc, @RequestParam String key)
			throws CartException, AuthorizationException {

		String role = userSessionService.getUserRole(key);
		if (!"USER".equals(role) && !"MANAGER".equals(role)) {
			throw new AuthorizationException("Access Denied: Only Employees can register food carts.");
		}

		System.out.println("Received Cart: " + fc);
		System.out.println("Customer in Cart: " + fc.getCustomer());

		Integer sessionId = userSessionService.getUserSessionId(key);

		if (fc == null) {
			throw new CartException("FoodCart object is null!");
		}

		if (fc.getCustomer() == null) {
			throw new CartException("Customer information is missing in the request!");
		}

		if (sessionId == null) {
			throw new AuthorizationException("Invalid session key!");
		}

		FoodCart savedCart = cartService.saveCart(fc);
		return new ResponseEntity<>(savedCart, HttpStatus.CREATED);
	}

	//  Only Employees can add items to a cart
	@PutMapping("/add/{cartId}/{itemId}")
	public ResponseEntity<FoodCart> addItemToCart(@PathVariable("cartId") Integer cartId,
												  @PathVariable("itemId") Integer itemId,
												  @RequestParam String key)
			throws CartException, ItemException, AuthorizationException {

		String role = userSessionService.getUserRole(key);
		if (!"USER".equals(role)) {
			throw new AuthorizationException("Access Denied: Only Employees can add items to carts.");
		}

		FoodCart updatedCart = cartService.addItem(cartId, itemId);
		return new ResponseEntity<>(updatedCart, HttpStatus.ACCEPTED);
	}

	//  Only Employees can remove a cart
	@DeleteMapping("/remove/{cartId}")
	public ResponseEntity<FoodCart> removeCart(@PathVariable("cartId") Integer cartId, @RequestParam String key)
			throws CartException, AuthorizationException {

		String role = userSessionService.getUserRole(key);
		if (!"USER".equals(role)) {
			throw new AuthorizationException("Access Denied: Only Employees can remove carts.");
		}

		FoodCart removedCart = cartService.clearCart(cartId);
		return new ResponseEntity<>(removedCart, HttpStatus.OK);
	}

	// Only Employees can view food carts
	@GetMapping("/view/{cartId}")
	public ResponseEntity<FoodCart> getCartByCartId(@PathVariable("cartId") Integer cartId, @RequestParam String key)
			throws AuthorizationException, CartException {

		String role = userSessionService.getUserRole(key);
		if (!"USER".equals(role)) {
			throw new AuthorizationException("Access Denied: Only Employees can view carts.");
		}

		Integer sessionId = userSessionService.getUserSessionId(key);
		if (sessionId != null) {
			FoodCart cart = cartService.viewCart(cartId);
			return new ResponseEntity<>(cart, HttpStatus.OK);
		} else {
			throw new CartException("Invalid session.");
		}
	}
}