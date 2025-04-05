package com.foodapp.service;

import com.foodapp.DTO.FeedbackDTO;
import com.foodapp.exceptions.CustomerException;
import com.foodapp.exceptions.OrderException;
import com.foodapp.model.*;
import com.foodapp.repository.CustomerDAO;
import com.foodapp.repository.FeedbackDAO;
import com.foodapp.repository.ItemDAO;
import com.foodapp.repository.OrderDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackServiceImpl {

    @Autowired
    private FeedbackDAO feedbackRepo;

    @Autowired
    private ItemDAO itemRepo;

    @Autowired
    private CustomerDAO customerRepo;

    public Feedback submitFeedback(FeedbackDTO dto) {
        Item item = itemRepo.findById(dto.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found"));
        Customer customer = customerRepo.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Feedback feedback = new Feedback();
        feedback.setItem(item);
        feedback.setCustomer(customer);
        feedback.setRating(dto.getRating());
        feedback.setComment(dto.getComment());
        return feedbackRepo.save(feedback);
    }

    public List<Feedback> getFeedbacksByRestaurant(Restaurant restaurant) {
        return feedbackRepo.findByItem_Category_Restaurant(restaurant);
    }
}
