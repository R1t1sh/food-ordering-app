package com.foodapp.service;

import com.foodapp.exceptions.CustomerException;
import com.foodapp.exceptions.ItemException;
import com.foodapp.exceptions.OrderException;
import com.foodapp.model.*;
import com.foodapp.repository.BillDAO;
import com.foodapp.repository.CustomerDAO;
import com.foodapp.repository.FoodCartDAO;
import com.foodapp.repository.OrderDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderDetailServiceImplTest {

    @Mock
    private OrderDAO orderDAO;

    @Mock
    private CustomerDAO customerDAO;

    @Mock
    private FoodCartDAO cartDAO;

    @Mock
    private BillDAO billDAO;

    @InjectMocks
    private OrderDetailServiceImpl orderService;

    private OrderDetails order;
    private Customer customer;
    private FoodCart cart;
    private Item item;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setCustomerId(1);

        cart = new FoodCart();
        cart.setCartId(1);
        cart.setCustomer(customer);
        cart.setItemList(Arrays.asList());

        order = new OrderDetails();
        order.setOrderId(1);
        order.setOrderDate(LocalDateTime.now());
        order.setCustomer(customer);

        item = new Item();
        item.setItemId(1);
        item.setQuantity(2);
        item.setCost(50.0);
    }

    @Test
    void testAddOrder_Success() throws OrderException {
        when(orderDAO.findById(order.getOrderId())).thenReturn(Optional.empty());
        when(orderDAO.save(order)).thenReturn(order);

        OrderDetails savedOrder = orderService.addOrder(order);

        assertNotNull(savedOrder);
        verify(orderDAO, times(1)).save(order);
    }

    @Test
    void testAddOrder_ThrowsException() {
        when(orderDAO.findById(order.getOrderId())).thenReturn(Optional.of(order));

        assertThrows(OrderException.class, () -> orderService.addOrder(order));
    }

    @Test
    void testViewOrder_Success() throws OrderException {
        when(orderDAO.findById(1)).thenReturn(Optional.of(order));

        OrderDetails foundOrder = orderService.viewOrder(1);

        assertNotNull(foundOrder);
        assertEquals(1, foundOrder.getOrderId());
    }

    @Test
    void testViewOrder_NotFound() {
        when(orderDAO.findById(1)).thenReturn(Optional.empty());

        assertThrows(OrderException.class, () -> orderService.viewOrder(1));
    }


    @Test
    void testRemoveOrder_NotFound() {
        when(orderDAO.findById(1)).thenReturn(Optional.empty());

        assertThrows(OrderException.class, () -> orderService.removeOrder(1));
    }

    @Transactional
    public OrderDetails checkoutOrder(Integer cartId) throws OrderException, ItemException {
        Optional<FoodCart> cartOpt = cartDAO.findById(cartId);
        if (!cartOpt.isPresent() || cartOpt.get().getItemList().isEmpty()) {
            throw new ItemException("Cart is empty or does not exist.");
        }
        FoodCart cart = cartOpt.get();

        OrderDetails order = new OrderDetails();
        order.setCustomer(cart.getCustomer());
        order.setOrderDate(LocalDateTime.now());

        List<Item> items = cart.getItemList();

        for (Item item : items) {
            item.setOrder(order);
            item.setFoodCart(null); // Detach from cart
        }

        order.setItems(new ArrayList<>(items));

        double totalAmount = items.stream().mapToDouble(item -> item.getCost() * item.getQuantity()).sum();
        int totalItems = items.stream().mapToInt(Item::getQuantity).sum();

        Bill bill = new Bill();
        bill.setTotalCost(totalAmount);
        bill.setTotalItem(totalItems);
        bill.setOrder(order);
        bill.setBillDate(LocalDateTime.now());
        bill.setStatus("PENDING");

        order.setBill(bill);

        OrderDetails savedOrder = orderDAO.save(order);

        cart.setItemList(new ArrayList<>()); // **Fix applied here**
        cartDAO.save(cart);

        return savedOrder;
    }

    @Test
    void testCheckoutOrder_EmptyCart() {
        when(cartDAO.findById(1)).thenReturn(Optional.of(cart));

        assertThrows(ItemException.class, () -> orderService.checkoutOrder(1));
    }
}
