package com.foodapp.controller;

import java.util.List;

import com.foodapp.exceptions.ItemException;
import com.foodapp.exceptions.CustomerException;
import com.foodapp.exceptions.OrderException;
import com.foodapp.model.Item;
import com.foodapp.model.OrderDetails;
import com.foodapp.service.OrderDetailService;

import com.foodapp.authexceptions.AuthorizationException;
import com.foodapp.authservice.UserSessionService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderDetailServiceController {

	@Autowired
	private OrderDetailService orderService;

	@Autowired
	private UserSessionService userSessionService;

	@PostMapping("/save")
	public ResponseEntity<OrderDetails> saveOrder(@RequestBody OrderDetails order, @RequestParam String key)
			throws OrderException, AuthorizationException {
		log.info("Saving new order");
		String role = userSessionService.getUserRole(key);
		if (!"USER".equals(role) && !"MANAGER".equals(role)) {
			log.warn("Unauthorized save attempt by role: {}", role);
			throw new AuthorizationException("Access Denied: Only Employees and Managers can place orders.");
		}

		Integer sessionId = userSessionService.getUserSessionId(key);
		if (sessionId == null) {
			log.error("Invalid session key: {}", key);
			throw new AuthorizationException("Invalid session key!");
		}

		OrderDetails savedOrder = orderService.addOrder(order);
		log.info("Order placed with ID: {}", savedOrder.getOrderId());
		return new ResponseEntity<>(savedOrder, HttpStatus.CREATED);
	}

	@PutMapping("/update")
	public ResponseEntity<OrderDetails> updateOrder(@RequestBody OrderDetails order, @RequestParam String key)
			throws OrderException, AuthorizationException {
		log.info("Updating order ID: {}", order.getOrderId());
		String role = userSessionService.getUserRole(key);
		if (!"USER".equals(role)) {
			log.warn("Unauthorized update attempt by role: {}", role);
			throw new AuthorizationException("Access Denied: Only Employees can update orders.");
		}
		return new ResponseEntity<>(orderService.updateOrder(order), HttpStatus.ACCEPTED);
	}

	@DeleteMapping("/remove/{orderId}")
	public ResponseEntity<OrderDetails> deleteOrder(@PathVariable Integer orderId, @RequestParam String key)
			throws OrderException, AuthorizationException {
		log.info("Deleting order ID: {}", orderId);
		String role = userSessionService.getUserRole(key);
		if (!"USER".equals(role)) {
			log.warn("Unauthorized delete attempt by role: {}", role);
			throw new AuthorizationException("Access Denied: Only Employees can remove orders.");
		}
		return new ResponseEntity<>(orderService.removeOrder(orderId), HttpStatus.ACCEPTED);
	}

	@GetMapping("/view/{orderId}")
	public ResponseEntity<OrderDetails> viewOrder(@PathVariable Integer orderId, @RequestParam String key)
			throws OrderException, AuthorizationException {
		log.info("Viewing order ID: {}", orderId);
		String role = userSessionService.getUserRole(key);
		if (!"USER".equals(role)) {
			log.warn("Unauthorized view attempt by role: {}", role);
			throw new AuthorizationException("Access Denied: Only Employees can view orders.");
		}
		return new ResponseEntity<>(orderService.viewOrder(orderId), HttpStatus.FOUND);
	}

	@PostMapping("/checkout/{cartId}")
	public ResponseEntity<OrderDetails> checkout(@PathVariable Integer cartId, @RequestParam String key)
			throws OrderException, AuthorizationException, ItemException, CustomerException {
		log.info("Checkout initiated for cart ID: {}", cartId);
		String role = userSessionService.getUserRole(key);
		if (!"USER".equals(role)) {
			log.warn("Unauthorized checkout attempt by role: {}", role);
			throw new AuthorizationException("Access Denied: Only Employees can perform checkout.");
		}
		OrderDetails order = orderService.checkoutOrder(cartId);
		log.info("Checkout completed with Order ID: {}", order.getOrderId());
		return new ResponseEntity<>(order, HttpStatus.CREATED);
	}
}
