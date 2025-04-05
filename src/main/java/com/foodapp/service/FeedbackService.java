package com.foodapp.service;

import com.foodapp.exceptions.CustomerException;
import com.foodapp.exceptions.OrderException;
import com.foodapp.model.Feedback;

import java.util.List;

public interface FeedbackService {
    Feedback submitFeedback(Integer customerId, Integer orderId, String comment, int rating) throws CustomerException, OrderException;

     List<Feedback> getFeedbackForRestaurant(Integer restaurantId);

}
