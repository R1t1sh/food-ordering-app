package com.foodapp.controller;

import com.foodapp.authexceptions.AuthorizationException;
import com.foodapp.authservice.UserSessionService;
import com.foodapp.exceptions.CartException;
import com.foodapp.exceptions.ItemException;
import com.foodapp.model.Customer;
import com.foodapp.model.FoodCart;
import com.foodapp.service.FoodCartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FoodCartServiceControllerTest {

    @Mock
    private FoodCartService cartService;

    @Mock
    private UserSessionService userSessionService;

    @InjectMocks
    private FoodCartServiceController cartController;

    private FoodCart foodCart;

    private final String EMPLOYEE_KEY = "EMPLOYEE_KEY";
    private final Integer CART_ID = 1;
    private final Integer ITEM_ID = 10;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        foodCart = new FoodCart();
        foodCart.setCartId(CART_ID);
        foodCart.setCustomer(new Customer());
    }

    @Test
    void testSaveCartDetails_Success() throws Exception {
        when(userSessionService.getUserRole(EMPLOYEE_KEY)).thenReturn("USER");
        when(userSessionService.getUserSessionId(EMPLOYEE_KEY)).thenReturn(123);
        when(cartService.saveCart(any(FoodCart.class))).thenReturn(foodCart);

        ResponseEntity<FoodCart> response = cartController.saveCartDetails(foodCart, EMPLOYEE_KEY);

        assertNotNull(response.getBody());
        assertEquals(CART_ID, response.getBody().getCartId());
        verify(cartService).saveCart(any(FoodCart.class));
    }

    @Test
    void testAddItemToCart_Success() throws Exception {
        when(userSessionService.getUserRole(EMPLOYEE_KEY)).thenReturn("USER");
        when(cartService.addItem(CART_ID, ITEM_ID)).thenReturn(foodCart);

        ResponseEntity<FoodCart> response = cartController.addItemToCart(CART_ID, ITEM_ID, EMPLOYEE_KEY);

        assertNotNull(response.getBody());
        assertEquals(CART_ID, response.getBody().getCartId());
        verify(cartService).addItem(CART_ID, ITEM_ID);
    }

    @Test
    void testRemoveCart_Success() throws Exception {
        when(userSessionService.getUserRole(EMPLOYEE_KEY)).thenReturn("USER");
        when(cartService.clearCart(CART_ID)).thenReturn(foodCart);

        ResponseEntity<FoodCart> response = cartController.removeCart(CART_ID, EMPLOYEE_KEY);

        assertNotNull(response.getBody());
        assertEquals(CART_ID, response.getBody().getCartId());
        verify(cartService).clearCart(CART_ID);
    }

    @Test
    void testGetCartById_Success() throws Exception {
        when(userSessionService.getUserRole(EMPLOYEE_KEY)).thenReturn("USER");
        when(userSessionService.getUserSessionId(EMPLOYEE_KEY)).thenReturn(123);
        when(cartService.viewCart(CART_ID)).thenReturn(foodCart);

        ResponseEntity<FoodCart> response = cartController.getCartByCartId(CART_ID, EMPLOYEE_KEY);

        assertNotNull(response.getBody());
        assertEquals(CART_ID, response.getBody().getCartId());
        verify(cartService).viewCart(CART_ID);
    }

    @Test
    void testGetCartById_InvalidSession() throws AuthorizationException {
        when(userSessionService.getUserRole(EMPLOYEE_KEY)).thenReturn("USER");
        when(userSessionService.getUserSessionId(EMPLOYEE_KEY)).thenReturn(null);

        assertThrows(CartException.class, () -> cartController.getCartByCartId(CART_ID, EMPLOYEE_KEY));
    }
}
