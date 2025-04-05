package com.foodapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodapp.model.Restaurant;
import com.foodapp.model.Address;

import java.util.Optional;


@Repository
public interface RestaurantDAO extends JpaRepository<Restaurant, Integer>{
    Optional<Restaurant> findByRestaurantNameAndAddress(String restaurantName, Address address);


}
