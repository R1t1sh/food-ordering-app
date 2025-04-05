package com.foodapp.controller;

import com.foodapp.exceptions.BillException;
import com.foodapp.exceptions.CustomerException;
import com.foodapp.exceptions.ItemException;
import com.foodapp.model.Bill;
import com.foodapp.service.BillService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class BillServiceControllerTest {

    @Mock
    private BillService billService;

    @InjectMocks
    private BillServiceController billController;

    private Bill bill;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bill = new Bill();
        bill.setBillId(1);
    }

    @Test
    void testGenerateBill_Success() throws BillException {
        when(billService.addBill(bill)).thenReturn(bill);

        ResponseEntity<Bill> response = billController.generateBill(bill);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(bill.getBillId(), response.getBody().getBillId());
        verify(billService).addBill(bill);
    }

    @Test
    void testUpdateBill_Success() throws BillException {
        when(billService.updateBill(bill)).thenReturn(bill);

        ResponseEntity<Bill> response = billController.updateBill(bill);

        assertEquals(202, response.getStatusCodeValue());
        assertEquals(bill.getBillId(), response.getBody().getBillId());
        verify(billService).updateBill(bill);
    }

    @Test
    void testRemoveBill_Success() throws BillException {
        when(billService.removeBill(1)).thenReturn(bill);

        ResponseEntity<Bill> response = billController.removeBill(1);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(bill.getBillId(), response.getBody().getBillId());
        verify(billService).removeBill(1);
    }

    @Test
    void testViewBill_Success() throws BillException {
        when(billService.viewBill(1)).thenReturn(bill);

        ResponseEntity<Bill> response = billController.viewBill(1);

        assertEquals(202, response.getStatusCodeValue());
        assertEquals(bill.getBillId(), response.getBody().getBillId());
        verify(billService).viewBill(1);
    }

    @Test
    void testGetTotalByCustomerId_Success() throws CustomerException, ItemException {
        String total = "₹250.00";
        when(billService.generateTotalBillById(10)).thenReturn(total);

        ResponseEntity<String> response = billController.getTotalByCustomerId(10);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(total, response.getBody());
        verify(billService).generateTotalBillById(10);
    }
}
