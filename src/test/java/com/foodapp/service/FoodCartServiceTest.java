package com.foodapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foodapp.exceptions.CartException;
import com.foodapp.exceptions.ItemException;
import com.foodapp.model.Customer;
import com.foodapp.model.FoodCart;
import com.foodapp.model.Item;
import com.foodapp.repository.CustomerDAO;
import com.foodapp.repository.FoodCartDAO;
import com.foodapp.repository.ItemDAO;

@ExtendWith(MockitoExtension.class)
class FoodCartServiceTest {

    @Mock
    private FoodCartDAO cartDAO;

    @Mock
    private ItemDAO itemDAO;

    @Mock
    private CustomerDAO customerDAO;

    @InjectMocks
    private FoodCartServiceImpl foodCartService;

    private FoodCart foodCart;
    private Customer customer;
    private Item item;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setCustomerId(1);

        foodCart = new FoodCart();
        foodCart.setCartId(100);
        foodCart.setCustomer(customer);
        foodCart.setItemList(new ArrayList<>());

        item = new Item();
        item.setItemId(200);
        item.setItemName("Pizza");
    }

    @Test
    void testSaveCart_Success() throws CartException {
        when(customerDAO.findById(customer.getCustomerId())).thenReturn(Optional.of(customer));
        when(cartDAO.save(any(FoodCart.class))).thenReturn(foodCart);

        FoodCart savedCart = foodCartService.saveCart(foodCart);

        assertNotNull(savedCart);
        assertEquals(100, savedCart.getCartId());
        verify(cartDAO, times(1)).save(foodCart);
    }

    @Test
    void testSaveCart_CustomerNotFound() {
        when(customerDAO.findById(customer.getCustomerId())).thenReturn(Optional.empty());

        CartException exception = assertThrows(CartException.class, () -> foodCartService.saveCart(foodCart));
        assertEquals("Customer with ID 1 not found!", exception.getMessage());
    }

    @Test
    void testViewCart_Success() throws CartException {
        when(cartDAO.findById(100)).thenReturn(Optional.of(foodCart));

        FoodCart retrievedCart = foodCartService.viewCart(100);

        assertNotNull(retrievedCart);
        assertEquals(100, retrievedCart.getCartId());
    }

    @Test
    void testViewCart_NotFound() {
        when(cartDAO.findById(100)).thenReturn(Optional.empty());

        CartException exception = assertThrows(CartException.class, () -> foodCartService.viewCart(100));
        assertEquals("No Cart found with ID: 100", exception.getMessage());
    }

    @Test
    void testClearCart_Success() throws CartException {
        when(cartDAO.findById(100)).thenReturn(Optional.of(foodCart));
        doNothing().when(cartDAO).delete(foodCart);

        FoodCart clearedCart = foodCartService.clearCart(100);
        assertNotNull(clearedCart);
    }

    @Test
    void testClearCart_NotFound() {
        when(cartDAO.findById(100)).thenReturn(Optional.empty());

        CartException exception = assertThrows(CartException.class, () -> foodCartService.clearCart(100));
        assertEquals("No Cart found with ID: 100", exception.getMessage());
    }

    @Test
    void testAddItem_Success() throws CartException, ItemException {
        when(cartDAO.findById(100)).thenReturn(Optional.of(foodCart));
        when(itemDAO.findById(200)).thenReturn(Optional.of(item));
        when(cartDAO.save(any(FoodCart.class))).thenReturn(foodCart);
        when(itemDAO.save(any(Item.class))).thenReturn(item);

        FoodCart updatedCart = foodCartService.addItem(100, 200);

        assertNotNull(updatedCart);
        assertTrue(updatedCart.getItemList().contains(item));
        verify(cartDAO, times(1)).save(foodCart);
        verify(itemDAO, times(1)).save(item);
    }

    @Test
    void testAddItem_CartNotFound() {
        when(cartDAO.findById(100)).thenReturn(Optional.empty());

        CartException exception = assertThrows(CartException.class, () -> foodCartService.addItem(100, 200));
        assertEquals("No Cart found with ID: 100", exception.getMessage());
    }

    @Test
    void testAddItem_ItemNotFound() {
        when(cartDAO.findById(100)).thenReturn(Optional.of(foodCart));
        when(itemDAO.findById(200)).thenReturn(Optional.empty());

        ItemException exception = assertThrows(ItemException.class, () -> foodCartService.addItem(100, 200));
        assertEquals("No Item found with ID: 200", exception.getMessage());
    }

    @Test
    void testAddItem_ItemAlreadyInDifferentCart() {
        FoodCart anotherCart = new FoodCart();
        anotherCart.setCartId(101);
        item.setFoodCart(anotherCart);

        when(cartDAO.findById(100)).thenReturn(Optional.of(foodCart));
        when(itemDAO.findById(200)).thenReturn(Optional.of(item));

        ItemException exception = assertThrows(ItemException.class, () -> foodCartService.addItem(100, 200));
        assertEquals("Item already belongs to another cart.", exception.getMessage());
    }
}
