package com.foodapp.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodapp.exceptions.CustomerException;
import com.foodapp.model.Customer;
import com.foodapp.repository.CustomerDAO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerDAO customerDAO;

	@Override
	public Customer addCustomer(Customer customer) throws CustomerException {
		customer.setCustomerId(null); // Hibernate auto-generates ID
		log.info("Attempting to add new customer");
		log.debug("Customer payload received: {}", customer);

		Customer savedCustomer = customerDAO.save(customer);
		log.info("Customer added successfully with ID: {}", savedCustomer.getCustomerId());
		return savedCustomer;
	}

	@Override
	public Customer updateCustomer(Customer customer) throws CustomerException {
		log.info("Attempting to update customer with ID: {}", customer.getCustomerId());
		Optional<Customer> opt = customerDAO.findById(customer.getCustomerId());

		if (opt.isPresent()) {
			Customer updatedCustomer = customerDAO.save(customer);
			log.info("Customer updated successfully");
			log.debug("Updated customer data: {}", updatedCustomer);
			return updatedCustomer;
		} else {
			log.error("Customer not found with ID: {}", customer.getCustomerId());
			throw new CustomerException("No such customer exists..");
		}
	}

	@Override
	public Customer removeCustomerById(Integer customerId) throws CustomerException {
		log.info("Attempting to remove customer with ID: {}", customerId);
		Optional<Customer> opt = customerDAO.findById(customerId);

		if (opt.isPresent()) {
			Customer customer = opt.get();
			customerDAO.delete(customer);
			log.info("Customer removed successfully with ID: {}", customerId);
			return customer;
		} else {
			log.error("Customer not found for removal with ID: {}", customerId);
			throw new CustomerException("No Customer found with ID: " + customerId);
		}
	}

	@Override
	public Customer viewCustomer(Integer customerId) throws CustomerException {
		log.info("Fetching customer details for ID: {}", customerId);
		Optional<Customer> opt = customerDAO.findById(customerId);

		if (opt.isPresent()) {
			Customer customer = opt.get();
			log.debug("Customer found: {}", customer);
			return customer;
		} else {
			log.warn("Customer not found with ID: {}", customerId);
			throw new CustomerException("No Customer found with ID: " + customerId);
		}
	}
}
