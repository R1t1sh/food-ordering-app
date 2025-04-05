package com.foodapp.service;

import com.foodapp.exceptions.CustomerException;
import com.foodapp.model.Address;
import com.foodapp.model.Customer;
import com.foodapp.model.FoodCart;
import com.foodapp.repository.CustomerDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class CustomerServiceTest {

    @Mock
    private CustomerDAO customerDAO;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer dummyCustomer;

    private final Integer VALID_CUSTOMER_ID = 1;
    private final Integer INVALID_CUSTOMER_ID = 100;

    @BeforeEach
    void setUp() {
        Address address = new Address("Street 1", "City", "State","Country", "123456");
        FoodCart foodCart = new FoodCart();
        dummyCustomer = new Customer(VALID_CUSTOMER_ID, "John Doe", 25, "Male", "9876543210", "john@example.com", address, foodCart);
    }

    @Test
    void testAddCustomer() throws CustomerException{
        when(customerDAO.save(any(Customer.class))).thenReturn(dummyCustomer);
        Customer savedCustomer = customerService.addCustomer(dummyCustomer);

        assertNotNull(savedCustomer);
        assertEquals("John Doe", savedCustomer.getFullName());
        verify(customerDAO, times(1)).save(dummyCustomer);
    }

    @Test
    void testUpdateCustomer_Found() throws CustomerException {
        // Arrange
        Integer existingCustomerId = 1; // Ensure this matches your actual ID
        Customer existingCustomer = new Customer();
        existingCustomer.setCustomerId(existingCustomerId);
        existingCustomer.setFullName("John Doe");

        when(customerDAO.findById(existingCustomerId)).thenReturn(Optional.of(existingCustomer));
        when(customerDAO.save(any(Customer.class))).thenReturn(existingCustomer);

        // Act
        Customer updatedCustomer = customerService.updateCustomer(existingCustomer);

        // Assert
        assertNotNull(updatedCustomer);
        assertEquals("John Doe", updatedCustomer.getFullName());

        // Verify interactions
        verify(customerDAO, times(1)).findById(existingCustomerId);
        verify(customerDAO, times(1)).save(existingCustomer);
    }


    @Test
    void testUpdateCustomer_NotFound() {
        Integer invalidCustomerId = 100; // Make sure this ID is the same everywhere

        Customer customerToUpdate = new Customer();
        customerToUpdate.setCustomerId(invalidCustomerId);  // Ensure same ID
        customerToUpdate.setFullName("Updated Name");

        when(customerDAO.findById(invalidCustomerId)).thenReturn(Optional.empty());

        assertThrows(CustomerException.class, () -> customerService.updateCustomer(customerToUpdate));

        verify(customerDAO, times(1)).findById(invalidCustomerId); // Ensure the correct ID was checked
    }


    @Test
    void testRemoveCustomerById_Found() throws CustomerException {
        when(customerDAO.findById(VALID_CUSTOMER_ID)).thenReturn(Optional.of(dummyCustomer));

        Customer deletedCustomer = customerService.removeCustomerById(VALID_CUSTOMER_ID);
        assertNotNull(deletedCustomer);
        assertEquals(VALID_CUSTOMER_ID, deletedCustomer.getCustomerId());
        verify(customerDAO, times(1)).delete(dummyCustomer);
    }

    @Test
    void testRemoveCustomerById_NotFound() throws CustomerException {
        when(customerDAO.findById(INVALID_CUSTOMER_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(CustomerException.class, () -> customerService.removeCustomerById(INVALID_CUSTOMER_ID));
        assertEquals("No Customer found with ID: " + INVALID_CUSTOMER_ID, exception.getMessage());
        verify(customerDAO, times(1)).findById(INVALID_CUSTOMER_ID);
    }

    @Test
    void testViewCustomer_Found() throws CustomerException{
        when(customerDAO.findById(VALID_CUSTOMER_ID)).thenReturn(Optional.of(dummyCustomer));

        Customer foundCustomer = customerService.viewCustomer(VALID_CUSTOMER_ID);
        assertNotNull(foundCustomer);
        assertEquals("John Doe", foundCustomer.getFullName());
        verify(customerDAO, times(1)).findById(VALID_CUSTOMER_ID);
    }

    @Test
    void testViewCustomer_NotFound() throws CustomerException{
        when(customerDAO.findById(INVALID_CUSTOMER_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(CustomerException.class, () -> customerService.viewCustomer(INVALID_CUSTOMER_ID));
        assertEquals("No Customer found with ID: " + INVALID_CUSTOMER_ID, exception.getMessage());
        verify(customerDAO, times(1)).findById(INVALID_CUSTOMER_ID);
    }
}
