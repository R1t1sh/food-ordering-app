package com.foodapp.controller;

import com.foodapp.exceptions.PaymentException;
import com.foodapp.model.Bill;
import com.foodapp.model.Payment;
import com.foodapp.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private Payment payment;
    private Bill bill;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        bill = new Bill();
        bill.setBillId(1);

        payment = new Payment();
        payment.setId(1);
        payment.setRazorpayOrderId("order_xyz");
        payment.setRazorpayPaymentId("pay_abc");
        payment.setRazorpaySignature("sig123");
        payment.setStatus("PENDING");
        payment.setCreatedAt(LocalDateTime.now());
        payment.setBill(bill);
    }

    @Test
    void testInitiatePayment_Success() throws PaymentException {
        when(paymentService.initiatePayment(1)).thenReturn(payment);

        ResponseEntity<Payment> response = paymentController.initiatePayment(1);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(payment.getId(), response.getBody().getId());
        assertEquals("PENDING", response.getBody().getStatus());
        verify(paymentService).initiatePayment(1);
    }

    @Test
    void testVerifyPayment_Success() throws PaymentException {
        String expectedMessage = "Payment verified successfully.";

        when(paymentService.verifyPayment("pay_abc", "order_xyz", "sig123")).thenReturn(expectedMessage);

        ResponseEntity<String> response = paymentController.verifyPayment("pay_abc", "order_xyz", "sig123");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedMessage, response.getBody());
        verify(paymentService).verifyPayment("pay_abc", "order_xyz", "sig123");
    }
}
