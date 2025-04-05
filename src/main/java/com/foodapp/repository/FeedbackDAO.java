package com.foodapp.repository;

import com.foodapp.model.Feedback;
import com.foodapp.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackDAO extends JpaRepository<Feedback, Integer> {

    List<Feedback> findByItem_Category_Restaurant(Restaurant restaurant);


}
