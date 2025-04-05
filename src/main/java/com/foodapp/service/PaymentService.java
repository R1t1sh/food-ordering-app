package com.foodapp.service;

import com.foodapp.exceptions.PaymentException;
import com.foodapp.model.Payment;

public interface PaymentService {
    Payment initiatePayment(Integer billId) throws PaymentException;
    String verifyPayment(String razorpayPaymentId, String razorpayOrderId, String razorpaySignature) throws PaymentException;
}
