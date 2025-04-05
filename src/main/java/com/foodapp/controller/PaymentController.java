package com.foodapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.foodapp.exceptions.PaymentException;
import com.foodapp.model.Payment;
import com.foodapp.service.PaymentService;


@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

//    @PreAuthorize("hasRole('USER') or hasRole('MANAGER')")
    @PostMapping("/initiate/{billId}")
    public ResponseEntity<Payment> initiatePayment(@PathVariable Integer billId) throws PaymentException {
        Payment payment = paymentService.initiatePayment(billId);
        return ResponseEntity.ok(payment);
    }

//    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(
            @RequestParam String razorpayPaymentId,
            @RequestParam String razorpayOrderId,
            @RequestParam String razorpaySignature) throws PaymentException {
        String message = paymentService.verifyPayment(razorpayPaymentId, razorpayOrderId, razorpaySignature);
        return ResponseEntity.ok(message);
    }
}
