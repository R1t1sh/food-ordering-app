package com.foodapp.controller;

import java.util.List;

import com.foodapp.exceptions.ItemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foodapp.authexceptions.AuthorizationException;
import com.foodapp.authservice.UserSessionService;
//import com.foodapp.authexceptions.AuthorizationException;
//import com.foodapp.authservice.UserSessionService;
import com.foodapp.exceptions.CustomerException;
import com.foodapp.exceptions.OrderException;
import com.foodapp.model.Item;
import com.foodapp.model.OrderDetails;
import com.foodapp.service.OrderDetailService;

@RestController
@RequestMapping("/order")
public class OrderDetailServiceController {
	
	@Autowired
	OrderDetailService orderService;
	
	@Autowired
	UserSessionService userSessionService;



	// Only EMPLOYEES and MANAGERS can place orders
	@PostMapping("/save")
	public ResponseEntity<OrderDetails> saveOrder(@RequestBody OrderDetails order, @RequestParam String key)
			throws OrderException, AuthorizationException {

		String role = userSessionService.getUserRole(key);
		if (!"USER".equals(role) && !"MANAGER".equals(role)) {
			throw new AuthorizationException("Access Denied: Only Employees and Managers can place orders.");
		}

		Integer sessionId = userSessionService.getUserSessionId(key);
		if (sessionId == null) {
			throw new AuthorizationException("Invalid session key!");
		}

		return new ResponseEntity<>(orderService.addOrder(order), HttpStatus.CREATED);
	}

	// Only EMPLOYEES can update orders
	@PutMapping("/update")
	public ResponseEntity<OrderDetails> updateOrder(@RequestBody OrderDetails order, @RequestParam String key)
			throws OrderException, AuthorizationException {

		String role = userSessionService.getUserRole(key);
		if (!"USER".equals(role)) {
			throw new AuthorizationException("Access Denied: Only Employees can update orders.");
		}

		return new ResponseEntity<>(orderService.updateOrder(order), HttpStatus.ACCEPTED);
	}

	// Only EMPLOYEES can remove orders
	@DeleteMapping("/remove/{orderId}")
	public ResponseEntity<OrderDetails> deleteOrder(@PathVariable("orderId") Integer orderId, @RequestParam String key)
			throws OrderException, AuthorizationException {

		String role = userSessionService.getUserRole(key);
		if (!"USER".equals(role)) {
			throw new AuthorizationException("Access Denied: Only Employees can remove orders.");
		}

		return new ResponseEntity<>(orderService.removeOrder(orderId), HttpStatus.ACCEPTED);
	}

	// Only EMPLOYEES can view specific orders
	@GetMapping("/view/{orderId}")
	public ResponseEntity<OrderDetails> viewOrder(@PathVariable("orderId") Integer orderId, @RequestParam String key)
			throws OrderException, AuthorizationException {

		String role = userSessionService.getUserRole(key);
		if (!"USER".equals(role)) {
			throw new AuthorizationException("Access Denied: Only Employees can view orders.");
		}

		return new ResponseEntity<>(orderService.viewOrder(orderId), HttpStatus.FOUND);
	}

	// Only MANAGERS can view all orders by a customer
	@GetMapping("/viewbycustomer/{customerId}")
	public ResponseEntity<List<Item>> viewAllOrders(@PathVariable("customerId") Integer customerId, @RequestParam String key)
			throws OrderException, CustomerException, AuthorizationException {

		String role = userSessionService.getUserRole(key);
		if (!"MANAGER".equals(role)) {
			throw new AuthorizationException("Access Denied: Only Managers can view all orders by a customer.");
		}

		return new ResponseEntity<>(orderService.viewAllOrdersByCustomer(customerId), HttpStatus.FOUND);
	}

	// Only EMPLOYEES can perform checkout
	@PostMapping("/checkout/{cartId}")
	public ResponseEntity<OrderDetails> checkout(@PathVariable("cartId") Integer cartId, @RequestParam String key)
			throws OrderException, AuthorizationException, ItemException,CustomerException{

		String role = userSessionService.getUserRole(key);
		if (!"USER".equals(role)) {
			throw new AuthorizationException("Access Denied: Only Employees can perform checkout.");
		}

		OrderDetails order = orderService.checkoutOrder(cartId);
		return new ResponseEntity<>(order, HttpStatus.CREATED);
	}
}
