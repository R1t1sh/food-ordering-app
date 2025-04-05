package com.foodapp.controller;

import com.foodapp.authexceptions.AuthorizationException;
import com.foodapp.authservice.UserSessionService;
import com.foodapp.exceptions.CartException;
import com.foodapp.exceptions.ItemException;
import com.foodapp.model.FoodCart;
import com.foodapp.service.FoodCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@Slf4j
public class FoodCartServiceController {

	@Autowired
	private FoodCartService cartService;

	@Autowired
	private UserSessionService userSessionService;

	@PostMapping("/register")
	public ResponseEntity<FoodCart> saveCartDetails(@RequestBody FoodCart fc, @RequestParam String key)
			throws CartException, AuthorizationException {

		log.info("Request received to register food cart for session key: {}", key);
		String role = userSessionService.getUserRole(key);
		if (!"USER".equals(role) && !"MANAGER".equals(role)) {
			log.warn("Unauthorized cart registration attempt by role: {}", role);
			throw new AuthorizationException("Access Denied: Only Employees can register food carts.");
		}

		Integer sessionId = userSessionService.getUserSessionId(key);

		if (fc == null || fc.getCustomer() == null) {
			log.error("Invalid FoodCart object received: {}", fc);
			throw new CartException("Cart or Customer information is missing!");
		}

		if (sessionId == null) {
			log.error("Invalid session key: {}", key);
			throw new AuthorizationException("Invalid session key!");
		}

		FoodCart savedCart = cartService.saveCart(fc);
		log.info("Cart saved successfully with ID: {}", savedCart.getCartId());
		return new ResponseEntity<>(savedCart, HttpStatus.CREATED);
	}

	@PutMapping("/add/{cartId}/{itemId}")
	public ResponseEntity<FoodCart> addItemToCart(@PathVariable Integer cartId,
												  @PathVariable Integer itemId,
												  @RequestParam String key)
			throws CartException, ItemException, AuthorizationException {

		log.info("Request to add Item ID: {} to Cart ID: {}", itemId, cartId);

		String role = userSessionService.getUserRole(key);
		if (!"USER".equals(role)) {
			log.warn("Unauthorized item-add attempt by role: {}", role);
			throw new AuthorizationException("Access Denied: Only Employees can add items to carts.");
		}

		FoodCart updatedCart = cartService.addItem(cartId, itemId);
		log.info("Item added to cart successfully");
		return new ResponseEntity<>(updatedCart, HttpStatus.ACCEPTED);
	}

	@DeleteMapping("/remove/{cartId}")
	public ResponseEntity<FoodCart> removeCart(@PathVariable Integer cartId, @RequestParam String key)
			throws CartException, AuthorizationException {

		log.info("Request to remove Cart ID: {}", cartId);

		String role = userSessionService.getUserRole(key);
		if (!"USER".equals(role)) {
			log.warn("Unauthorized delete attempt for Cart ID: {} by role: {}", cartId, role);
			throw new AuthorizationException("Access Denied: Only Employees can remove carts.");
		}

		FoodCart removedCart = cartService.clearCart(cartId);
		log.info("Cart removed successfully");
		return new ResponseEntity<>(removedCart, HttpStatus.OK);
	}

	@GetMapping("/view/{cartId}")
	public ResponseEntity<FoodCart> getCartByCartId(@PathVariable Integer cartId, @RequestParam String key)
			throws AuthorizationException, CartException {

		log.info("Fetching Cart ID: {} for session key: {}", cartId, key);

		String role = userSessionService.getUserRole(key);
		if (!"USER".equals(role)) {
			log.warn("Unauthorized cart view attempt by role: {}", role);
			throw new AuthorizationException("Access Denied: Only Employees can view carts.");
		}

		Integer sessionId = userSessionService.getUserSessionId(key);
		if (sessionId != null) {
			FoodCart cart = cartService.viewCart(cartId);
			log.debug("Cart fetched: {}", cart);
			return new ResponseEntity<>(cart, HttpStatus.OK);
		} else {
			log.error("Session ID not found for key: {}", key);
			throw new CartException("Invalid session.");
		}
	}
}
