package com.foodapp.service;

import com.foodapp.DTO.FeedbackDTO;
import com.foodapp.model.*;
import com.foodapp.repository.CustomerDAO;
import com.foodapp.repository.FeedbackDAO;
import com.foodapp.repository.ItemDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FeedbackServiceImpl {

    @Autowired
    private FeedbackDAO feedbackRepo;

    @Autowired
    private ItemDAO itemRepo;

    @Autowired
    private CustomerDAO customerRepo;

    public Feedback submitFeedback(FeedbackDTO dto) {
        log.info("Submitting feedback: {}", dto);

        Item item = itemRepo.findById(dto.getItemId())
                .orElseThrow(() -> {
                    log.error("Item not found with ID: {}", dto.getItemId());
                    return new RuntimeException("Item not found");
                });

        Customer customer = customerRepo.findById(dto.getCustomerId())
                .orElseThrow(() -> {
                    log.error("Customer not found with ID: {}", dto.getCustomerId());
                    return new RuntimeException("Customer not found");
                });

        Feedback feedback = new Feedback();
        feedback.setItem(item);
        feedback.setCustomer(customer);
        feedback.setRating(dto.getRating());
        feedback.setComment(dto.getComment());

        Feedback saved = feedbackRepo.save(feedback);
        log.info("Feedback submitted successfully with ID: {}", saved.getFeedbackId());
        return saved;
    }

    public List<Feedback> getFeedbacksByRestaurant(Restaurant restaurant) {
        log.info("Fetching feedbacks for restaurant ID: {}", restaurant.getRestaurantId());
        List<Feedback> list = feedbackRepo.findByItem_Category_Restaurant(restaurant);
        log.debug("Found {} feedback(s) for restaurant", list.size());
        return list;
    }
}
