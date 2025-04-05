package com.foodapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.foodapp.exceptions.BillException;
import com.foodapp.exceptions.CustomerException;
import com.foodapp.exceptions.ItemException;
import com.foodapp.model.Bill;
import com.foodapp.model.Customer;
import com.foodapp.model.FoodCart;
import com.foodapp.model.Item;
import com.foodapp.model.OrderDetails;
import com.foodapp.repository.BillDAO;
import com.foodapp.repository.CustomerDAO;

public class BillServiceTest {

    @Mock
    private BillDAO billDAO;

    @Mock
    private CustomerDAO customerDAO;

    @InjectMocks
    private BillServiceImpl billService;

    private Customer customer;
    private FoodCart foodCart;
    private List<Item> items;
    private Bill bill;
    private OrderDetails orderDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create a customer
        customer = new Customer();
        customer.setCustomerId(1);
        customer.setFullName("John Doe");

        // Create an order
        orderDetails = new OrderDetails();
        orderDetails.setOrderId(101);

        // Create a food cart
        foodCart = new FoodCart();
        foodCart.setCartId(1);

        // Create a list of items
        items = new ArrayList<>();
        Item item = new Item();
        item.setItemId(1);
        item.setItemName("Pizza");
        item.setQuantity(1);
        item.setCost(200.0);
        item.setFoodCart(foodCart);

        item.setItemId(1);
        item.setItemName("Pizza");
        item.setQuantity(1);
        item.setCost(200.0);
        item.setFoodCart(foodCart);

        // Associate the cart with items
        foodCart.setItemList(items);

        // Associate customer with cart
        customer.setFoodCart(foodCart);

        // Create a bill
        bill = new Bill();
        bill.setBillId(1);
        bill.setBillDate(LocalDateTime.now());
        bill.setTotalCost(400.0);
        bill.setOrder(orderDetails);
    }

    // ✅ Test case: Add Bill Successfully
    @Test
    void testAddBill_Success() throws BillException {
        when(billDAO.findByOrder_OrderId(bill.getOrder().getOrderId())).thenReturn(Optional.empty());
        when(billDAO.save(bill)).thenReturn(bill);

        Bill savedBill = billService.addBill(bill);

        assertNotNull(savedBill);
        assertEquals(1, savedBill.getBillId());
    }

    // ✅ Test case: Add Bill with Existing Order
    @Test
    void testAddBill_ExistingBill() {
        when(billDAO.findByOrder_OrderId(bill.getOrder().getOrderId())).thenReturn(Optional.of(bill));

        Exception exception = assertThrows(BillException.class, () -> {
            billService.addBill(bill);
        });

        assertEquals("Bill already exists for this order.", exception.getMessage());
    }

    // ✅ Test case: Add Bill with Null Order
    @Test
    void testAddBill_NullOrder() {
        bill.setOrder(null);

        Exception exception = assertThrows(BillException.class, () -> {
            billService.addBill(bill);
        });

        assertEquals("Order ID must not be null!", exception.getMessage());
    }

    // ✅ Test case: Update Bill Successfully
    @Test
    void testUpdateBill_Success() throws BillException {
        when(billDAO.findById(bill.getBillId())).thenReturn(Optional.of(bill));
        when(billDAO.save(bill)).thenReturn(bill);

        Bill updatedBill = billService.updateBill(bill);
        assertNotNull(updatedBill);
        assertEquals(1, updatedBill.getBillId());
    }

    // ✅ Test case: Update Non-Existing Bill
    @Test
    void testUpdateBill_BillNotFound() {
        when(billDAO.findById(bill.getBillId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(BillException.class, () -> {
            billService.updateBill(bill);
        });

        assertEquals("Bill doesn't exists..", exception.getMessage());
    }

    // ✅ Test case: Remove Bill Successfully
    @Test
    void testRemoveBill_Success() throws BillException {
        when(billDAO.findById(bill.getBillId())).thenReturn(Optional.of(bill));

        Bill removedBill = billService.removeBill(1);
        assertNotNull(removedBill);
        assertEquals(1, removedBill.getBillId());

        verify(billDAO, times(1)).delete(bill);
    }

    // ✅ Test case: Remove Non-Existing Bill
    @Test
    void testRemoveBill_BillNotFound() {
        when(billDAO.findById(bill.getBillId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(BillException.class, () -> {
            billService.removeBill(1);
        });

        assertEquals("Bill not found with ID: 1", exception.getMessage());
    }

    // ✅ Test case: View Bill Successfully
    @Test
    void testViewBill_Success() throws BillException {
        when(billDAO.findById(bill.getBillId())).thenReturn(Optional.of(bill));

        Bill foundBill = billService.viewBill(1);
        assertNotNull(foundBill);
        assertEquals(1, foundBill.getBillId());
    }

    // ✅ Test case: View Non-Existing Bill
    @Test
    void testViewBill_BillNotFound() {
        when(billDAO.findById(bill.getBillId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(BillException.class, () -> {
            billService.viewBill(1);
        });

        assertEquals("Bill not found with ID: 1", exception.getMessage());
    }

    public String generateTotalBillById(Integer customerId) throws CustomerException, ItemException {
        Customer customer = customerDAO.findById(customerId)
                .orElseThrow(() -> new CustomerException("No Customer found with ID: " + customerId));

        FoodCart foodCart = customer.getFoodCart();
        if (foodCart == null) {
            throw new ItemException("No order items found for " + customer.getFullName());
        }

        List<Item> items = foodCart.getItemList();
        if (items == null || items.isEmpty()) {
            throw new ItemException("No order items found for " + customer.getFullName());
        }

        double totalCost = items.stream().mapToDouble(Item::getCost).sum();
        return "The total bill for " + customer.getFullName() + " is " + totalCost;
    }


    // ✅ Test case: Generate Total Bill for Empty Cart
    @Test
    void testGenerateTotalBillById_NoItems() {
        foodCart.setItemList(new ArrayList<>());

        when(customerDAO.findById(1)).thenReturn(Optional.of(customer));

        Exception exception = assertThrows(ItemException.class, () -> {
            billService.generateTotalBillById(1);
        });

        assertEquals("No order items found for John Doe", exception.getMessage());
    }



    // ✅ Test case: Generate Total Bill for Non-Existing Customer
    @Test
    void testGenerateTotalBillById_CustomerNotFound() {
        when(customerDAO.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(CustomerException.class, () -> {
            billService.generateTotalBillById(1);
        });

        assertEquals("No Customer found with ID: 1", exception.getMessage());
    }
}
