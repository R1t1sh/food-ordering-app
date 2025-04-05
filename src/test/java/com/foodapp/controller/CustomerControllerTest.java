package com.foodapp.controller;

import com.foodapp.authservice.UserSessionServiceImpl;
import com.foodapp.exceptions.CustomerException;
import com.foodapp.authexceptions.AuthorizationException;
import com.foodapp.model.Customer;
import com.foodapp.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class CustomerServiceControllerTest {

    @Mock
    private CustomerService customerService;

    @Mock
    private UserSessionServiceImpl userSessionService;

    @InjectMocks
    private CustomerServiceController customerController;

    private Customer dummyCustomer;
    private final Integer VALID_CUSTOMER_ID = 102;
    private final Integer INVALID_CUSTOMER_ID = 999;
    private final String VALID_KEY = "valid-key";
    private final String INVALID_KEY = "invalid-key";

    @BeforeEach
    void setUp() {
        dummyCustomer = new Customer();
        dummyCustomer.setCustomerId(VALID_CUSTOMER_ID);
        dummyCustomer.setFullName("Rahul Sharma");
    }

    @Test
    void testAddCustomer_Success() throws CustomerException, AuthorizationException {
        when(userSessionService.getUserRole(VALID_KEY)).thenReturn("USER");
        when(customerService.addCustomer(any(Customer.class))).thenReturn(dummyCustomer);

        ResponseEntity<Customer> response = customerController.addCustomer(dummyCustomer, VALID_KEY);

        assertNotNull(response.getBody());
        assertEquals("Rahul Sharma", response.getBody().getFullName());
        verify(customerService, times(1)).addCustomer(any(Customer.class));
    }

    @Test
    void testAddCustomer_AuthorizationFailure() throws AuthorizationException, CustomerException {
        when(userSessionService.getUserRole(INVALID_KEY)).thenReturn("ADMIN");

        AuthorizationException exception = assertThrows(AuthorizationException.class, () ->
                customerController.addCustomer(dummyCustomer, INVALID_KEY));

        assertEquals("Access Denied: Only Employees can add customers.", exception.getMessage());
        verify(customerService, never()).addCustomer(any(Customer.class));
    }

    @Test
    void testUpdateCustomer_Success() throws CustomerException, AuthorizationException {
        when(userSessionService.getUserRole(VALID_KEY)).thenReturn("USER");
        when(customerService.updateCustomer(any(Customer.class))).thenReturn(dummyCustomer);

        ResponseEntity<Customer> response = customerController.updateCustomer(dummyCustomer, VALID_KEY);

        assertNotNull(response.getBody());
        assertEquals("Rahul Sharma", response.getBody().getFullName());
        verify(customerService, times(1)).updateCustomer(any(Customer.class));
    }

    @Test
    void testRemoveCustomer_Success() throws CustomerException, AuthorizationException {
        when(userSessionService.getUserRole(VALID_KEY)).thenReturn("ADMIN");
        when(customerService.removeCustomerById(VALID_CUSTOMER_ID)).thenReturn(dummyCustomer);

        ResponseEntity<Customer> response = customerController.removeCustomer(VALID_CUSTOMER_ID, VALID_KEY);

        assertNotNull(response.getBody());
        assertEquals("Rahul Sharma", response.getBody().getFullName());
        verify(customerService, times(1)).removeCustomerById(VALID_CUSTOMER_ID);
    }

    @Test
    void testViewCustomer_Success() throws CustomerException, AuthorizationException {
        when(userSessionService.getUserRole(VALID_KEY)).thenReturn("USER");
        when(customerService.viewCustomer(VALID_CUSTOMER_ID)).thenReturn(dummyCustomer);

        ResponseEntity<Customer> response = customerController.viewCustomer(VALID_CUSTOMER_ID, VALID_KEY);

        assertNotNull(response.getBody());
        assertEquals("Rahul Sharma", response.getBody().getFullName());
        verify(customerService, times(1)).viewCustomer(VALID_CUSTOMER_ID);
    }

    @Test
    void testViewCustomer_NotFound() throws CustomerException, AuthorizationException {
        when(userSessionService.getUserRole(VALID_KEY)).thenReturn("USER");
        when(customerService.viewCustomer(INVALID_CUSTOMER_ID)).thenThrow(new CustomerException("Customer not found"));

        Exception exception = assertThrows(CustomerException.class, () ->
                customerController.viewCustomer(INVALID_CUSTOMER_ID, VALID_KEY));

        assertEquals("Customer not found", exception.getMessage());
        verify(customerService, times(1)).viewCustomer(INVALID_CUSTOMER_ID);
    }
}
