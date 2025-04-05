package com.foodapp.controller;

import com.foodapp.DTO.FeedbackDTO;
import com.foodapp.model.Feedback;
import com.foodapp.model.Restaurant;
import com.foodapp.service.FeedbackServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedback")
@Slf4j
public class FeedbackController {

    @Autowired
    private FeedbackServiceImpl feedbackService;

    @PostMapping("/submit")
    public ResponseEntity<Feedback> submitFeedback(@RequestBody FeedbackDTO dto) {
        log.info("Submitting feedback for Item ID: {}, Customer ID: {}", dto.getItemId(), dto.getCustomerId());
        Feedback saved = feedbackService.submitFeedback(dto);
        log.info("Feedback submitted with ID: {}", saved.getFeedbackId());
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<Feedback>> getByRestaurant(@PathVariable Integer restaurantId) {
        log.info("Fetching feedback for Restaurant ID: {}", restaurantId);

        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantId(restaurantId);

        List<Feedback> feedbacks = feedbackService.getFeedbacksByRestaurant(restaurant);
        log.debug("Found {} feedback(s)", feedbacks.size());
        return ResponseEntity.ok(feedbacks);
    }
}
