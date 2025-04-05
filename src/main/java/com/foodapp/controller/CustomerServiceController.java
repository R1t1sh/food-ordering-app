package com.foodapp.controller;

import com.foodapp.authexceptions.AuthorizationException;
import com.foodapp.authservice.UserSessionServiceImpl;
import com.foodapp.exceptions.CustomerException;
import com.foodapp.model.Customer;
import com.foodapp.service.CustomerService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
@Slf4j
public class CustomerServiceController {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private UserSessionServiceImpl userSessionService;

	@PostMapping("/add")
	public ResponseEntity<Customer> addCustomer(@RequestBody Customer customer, @RequestParam String key) throws CustomerException, AuthorizationException {
		log.info("Received request to add customer");
		log.debug("Customer payload: {}", customer);

		String role = userSessionService.getUserRole(key);
		if (!"USER".equals(role)) {
			log.warn("Unauthorized attempt to add customer by role: {}", role);
			throw new AuthorizationException("Access Denied: Only Employees can add customers.");
		}

		Customer newCustomer = customerService.addCustomer(customer);
		log.info("Customer added with ID: {}", newCustomer.getCustomerId());
		return new ResponseEntity<>(newCustomer, HttpStatus.CREATED);
	}

	@PutMapping("/update")
	public ResponseEntity<Customer> updateCustomer(@RequestBody Customer customer, @RequestParam String key) throws CustomerException, AuthorizationException {
		log.info("Updating customer ID: {}", customer.getCustomerId());

		String role = userSessionService.getUserRole(key);
		if (!"USER".equals(role)) {
			log.warn("Unauthorized update attempt by role: {}", role);
			throw new AuthorizationException("Access Denied: Only Employees can update customers.");
		}

		Customer updatedCustomer = customerService.updateCustomer(customer);
		log.info("Customer updated successfully");
		return new ResponseEntity<>(updatedCustomer, HttpStatus.ACCEPTED);
	}

	@DeleteMapping("/remove/{customerId}")
	public ResponseEntity<Customer> removeCustomer(@PathVariable Integer customerId, @RequestParam String key) throws CustomerException, AuthorizationException {
		log.info("Removing customer with ID: {}", customerId);

		String role = userSessionService.getUserRole(key);
		if (!"ADMIN".equals(role) && !"USER".equals(role)) {
			log.warn("Unauthorized delete attempt by role: {}", role);
			throw new AuthorizationException("Access Denied: Only ADMIN or Employees can remove customers.");
		}

		Customer removedCustomer = customerService.removeCustomerById(customerId);
		log.info("Customer removed successfully");
		return new ResponseEntity<>(removedCustomer, HttpStatus.OK);
	}

	@GetMapping("/view/{customerId}")
	public ResponseEntity<Customer> viewCustomer(@PathVariable Integer customerId, @RequestParam String key) throws CustomerException, AuthorizationException {
		log.info("Fetching customer with ID: {}", customerId);

		String role = userSessionService.getUserRole(key);
		Integer sessionId = userSessionService.getUserSessionId(key);
		if ("ADMIN".equals(role) || "USER".equals(role)) {
			Customer customer = customerService.viewCustomer(customerId);
			log.debug("Customer details: {}", customer);
			return new ResponseEntity<>(customer, HttpStatus.OK);
		}

		log.warn("Invalid role or session for customer view");
		throw new AuthorizationException("Access Denied: You are not authorized to view this customer.");
	}
}
