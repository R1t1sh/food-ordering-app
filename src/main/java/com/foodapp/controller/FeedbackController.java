package com.foodapp.controller;

import com.foodapp.DTO.FeedbackDTO;
import com.foodapp.exceptions.CustomerException;
import com.foodapp.exceptions.OrderException;
import com.foodapp.model.Feedback;
import com.foodapp.model.Restaurant;
import com.foodapp.service.FeedbackService;
import com.foodapp.service.FeedbackServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackServiceImpl feedbackService;

    @PostMapping("/submit")
    public ResponseEntity<Feedback> submitFeedback(@RequestBody FeedbackDTO dto) {
        Feedback saved = feedbackService.submitFeedback(dto);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<Feedback>> getByRestaurant(@PathVariable Integer restaurantId) {
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantId(restaurantId); // Only ID is required to query

        List<Feedback> feedbacks = feedbackService.getFeedbacksByRestaurant(restaurant);
        return ResponseEntity.ok(feedbacks);
    }
}
