package com.foodapp.service;

import com.foodapp.exceptions.BillException;
import com.foodapp.exceptions.PaymentException;
import com.foodapp.model.Bill;
import com.foodapp.model.Payment;
import com.foodapp.repository.BillDAO;
import com.foodapp.repository.PaymentRepository;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final String RAZORPAY_KEY_ID = "rzp_test_DJomFwaN8Ohchs";
    private static final String RAZORPAY_KEY_SECRET = "zGUN6s3jVK9bGFTZXh57vfZo";

    @Autowired
    private BillDAO billRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public Payment initiatePayment(Integer billId) throws PaymentException {
        Optional<Bill> billOpt = billRepository.findById(billId);
        if (!billOpt.isPresent()) {
            throw new PaymentException("Bill not found with ID: " + billId);
        }

        Bill bill = billOpt.get();
        if (!"PENDING".equals(bill.getStatus())) {
            throw new PaymentException("Payment has already been completed for this bill.");
        }

        try {
            RazorpayClient razorpay = new RazorpayClient(RAZORPAY_KEY_ID, RAZORPAY_KEY_SECRET);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", bill.getTotalCost() * 100); // Amount in paise
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "txn_" + billId);
            orderRequest.put("payment_capture", 1);

            Order order = razorpay.orders.create(orderRequest);

            Payment payment = new Payment(bill, order.get("id"));
            paymentRepository.save(payment);

            return payment;
        } catch (RazorpayException e) {
            throw new PaymentException("Failed to initiate payment: " + e.getMessage());
        }
    }

    @Override
    public String verifyPayment(String razorpayPaymentId, String razorpayOrderId, String razorpaySignature) throws PaymentException {
        Optional<Payment> paymentOpt = paymentRepository.findByRazorpayOrderId(razorpayOrderId);
        if (!paymentOpt.isPresent()) {
            throw new PaymentException("Invalid payment verification request.");
        }

        Payment payment = paymentOpt.get();
        if (!"PENDING".equals(payment.getStatus())) {
            throw new PaymentException("Payment has already been processed.");
        }

       //will be done externally from razorpay dashboard or from UI
        payment.setRazorpayPaymentId(razorpayPaymentId);
        payment.setRazorpaySignature(razorpaySignature);
        payment.setStatus("COMPLETED");


        Bill bill = payment.getBill();
        bill.setStatus("PAID");

        if (bill.getOrder() != null) {
            bill.getOrder().setOrderStatus("PAID");
        }

        paymentRepository.save(payment);
        billRepository.save(bill);

        return "Payment verified successfully!";
    }
}
