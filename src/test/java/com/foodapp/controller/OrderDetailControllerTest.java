package com.foodapp.controller;

import com.foodapp.authservice.UserSessionService;
import com.foodapp.exceptions.CustomerException;
import com.foodapp.exceptions.ItemException;
import com.foodapp.exceptions.OrderException;
import com.foodapp.model.Item;
import com.foodapp.model.OrderDetails;
import com.foodapp.service.OrderDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderDetailServiceControllerTest {

    @Mock
    private OrderDetailService orderService;

    @Mock
    private UserSessionService userSessionService;

    @InjectMocks
    private OrderDetailServiceController orderController;

    private final String EMPLOYEE_KEY = "EMPLOYEE_KEY";
    private final String MANAGER_KEY = "MANAGER_KEY";

    private final Integer ORDER_ID = 101;
    private final Integer CUSTOMER_ID = 202;
    private final Integer CART_ID = 303;

    private OrderDetails order;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        order = new OrderDetails();
        order.setOrderId(ORDER_ID);
    }

    @Test
    void testSaveOrder_Success() throws Exception {
        when(userSessionService.getUserRole(EMPLOYEE_KEY)).thenReturn("USER");
        when(userSessionService.getUserSessionId(EMPLOYEE_KEY)).thenReturn(1);
        when(orderService.addOrder(order)).thenReturn(order);

        ResponseEntity<OrderDetails> response = orderController.saveOrder(order, EMPLOYEE_KEY);

        assertEquals(ORDER_ID, response.getBody().getOrderId());
        verify(orderService).addOrder(order);
    }

    @Test
    void testUpdateOrder_Success() throws Exception {
        when(userSessionService.getUserRole(EMPLOYEE_KEY)).thenReturn("USER");
        when(orderService.updateOrder(order)).thenReturn(order);

        ResponseEntity<OrderDetails> response = orderController.updateOrder(order, EMPLOYEE_KEY);

        assertEquals(ORDER_ID, response.getBody().getOrderId());
        verify(orderService).updateOrder(order);
    }

    @Test
    void testDeleteOrder_Success() throws Exception {
        when(userSessionService.getUserRole(EMPLOYEE_KEY)).thenReturn("USER");
        when(orderService.removeOrder(ORDER_ID)).thenReturn(order);

        ResponseEntity<OrderDetails> response = orderController.deleteOrder(ORDER_ID, EMPLOYEE_KEY);

        assertEquals(ORDER_ID, response.getBody().getOrderId());
        verify(orderService).removeOrder(ORDER_ID);
    }

    @Test
    void testViewOrder_Success() throws Exception {
        when(userSessionService.getUserRole(EMPLOYEE_KEY)).thenReturn("USER");
        when(orderService.viewOrder(ORDER_ID)).thenReturn(order);

        ResponseEntity<OrderDetails> response = orderController.viewOrder(ORDER_ID, EMPLOYEE_KEY);

        assertEquals(ORDER_ID, response.getBody().getOrderId());
        verify(orderService).viewOrder(ORDER_ID);
    }

    @Test
    void testViewAllOrdersByCustomer_Success() throws Exception {
        List<Item> items = Collections.singletonList(new Item());
        when(userSessionService.getUserRole(MANAGER_KEY)).thenReturn("MANAGER");
        when(orderService.viewAllOrdersByCustomer(CUSTOMER_ID)).thenReturn(items);

        ResponseEntity<List<Item>> response = orderController.viewAllOrders(CUSTOMER_ID, MANAGER_KEY);

        assertEquals(1, response.getBody().size());
        verify(orderService).viewAllOrdersByCustomer(CUSTOMER_ID);
    }

    @Test
    void testCheckout_Success() throws Exception {
        when(userSessionService.getUserRole(EMPLOYEE_KEY)).thenReturn("USER");
        when(orderService.checkoutOrder(CART_ID)).thenReturn(order);

        ResponseEntity<OrderDetails> response = orderController.checkout(CART_ID, EMPLOYEE_KEY);

        assertEquals(ORDER_ID, response.getBody().getOrderId());
        verify(orderService).checkoutOrder(CART_ID);
    }
}
