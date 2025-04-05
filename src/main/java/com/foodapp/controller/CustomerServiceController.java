package com.foodapp.controller;

import com.foodapp.authexceptions.AuthorizationException;
import com.foodapp.authservice.UserSessionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.foodapp.exceptions.CustomerException;
import com.foodapp.model.Customer;
import com.foodapp.service.CustomerService;



@RestController
@RequestMapping("/customer")
public class CustomerServiceController {
	
	@Autowired
	CustomerService customerService;


	@Autowired
	private UserSessionServiceImpl userSessionService;
	
	
	@PostMapping("/add")
	public ResponseEntity<Customer> addCustomer(@RequestBody Customer customer, @RequestParam String key) throws CustomerException, AuthorizationException {
		System.out.println("Received JSON: " + customer);
		String role = userSessionService.getUserRole(key);
		if (!"USER".equals(role)) {
			throw new AuthorizationException("Access Denied: Only Employees can add customers.");
		}
		Customer newCustomer = customerService.addCustomer(customer);
		 return new ResponseEntity<Customer>(newCustomer, HttpStatus.CREATED);
	}
	
	
	@PutMapping("/update")
	public ResponseEntity<Customer> updateCustomer(@RequestBody Customer customer,@RequestParam String key) throws CustomerException,AuthorizationException{

		String role = userSessionService.getUserRole(key);
		if (!"USER".equals(role)) {
			throw new AuthorizationException("Access Denied: Only Employees can update customers.");
		}

		Customer updatedCustomer = customerService.updateCustomer(customer);
		 return new ResponseEntity<Customer>(updatedCustomer, HttpStatus.ACCEPTED);
	}
	
	
	@DeleteMapping("/remove/{customerId}")
	public ResponseEntity<Customer> removeCustomer(@PathVariable("customerId") Integer customerId,@RequestParam String key) throws CustomerException,AuthorizationException{


		String role = userSessionService.getUserRole(key);
		if (!"ADMIN".equals(role) && !"USER".equals(role)) {
			throw new AuthorizationException("Access Denied: Only ADMIN or Employees can remove customers.");
		}
		 Customer removedCustomer = customerService.removeCustomerById(customerId);
		 return new ResponseEntity<Customer>(removedCustomer, HttpStatus.OK);
	}
	
	
	@GetMapping("/view/{customerId}")
	public ResponseEntity<Customer> viewCustomer(@PathVariable("customerId") Integer customerId,@RequestParam String key) throws CustomerException,AuthorizationException{


		String role = userSessionService.getUserRole(key);
		Integer sessionId = userSessionService.getUserSessionId(key);
		if ("ADMIN".equals(role) || "USER".equals(role)) {
			Customer customer = customerService.viewCustomer(customerId);
			return new ResponseEntity<>(customer, HttpStatus.OK);
		}
		 Customer customer = customerService.viewCustomer(customerId);
		 return new ResponseEntity<Customer>(customer, HttpStatus.OK);
	}

}
