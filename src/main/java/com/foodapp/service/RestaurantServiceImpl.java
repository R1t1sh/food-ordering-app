package com.foodapp.service;

import java.util.Optional;

import com.foodapp.exceptions.RestaurantException;
import com.foodapp.model.Address;
import com.foodapp.model.Restaurant;
import com.foodapp.repository.AddressDAO;
import com.foodapp.repository.RestaurantDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RestaurantServiceImpl implements RestaurantService {

	@Autowired
	private RestaurantDAO restDAO;

	@Autowired
	private AddressDAO addressDAO;

	@Override
	public Restaurant addRestaurant(Restaurant restaurant) throws RestaurantException {
		log.info("Attempting to add new restaurant: {}", restaurant.getRestaurantName());

		if (restaurant.getAddress() == null) {
			log.error("Address is null while adding restaurant");
			throw new RestaurantException("Address is required to add a restaurant.");
		}

		Address address;

		if (restaurant.getAddress().getAddressId() == null) {
			address = addressDAO.save(restaurant.getAddress());
			log.debug("New address saved with ID: {}", address.getAddressId());
		} else {
			Integer addressId = restaurant.getAddress().getAddressId();
			address = addressDAO.findById(addressId)
					.orElseThrow(() -> {
						log.error("Address not found with ID: {}", addressId);
						return new RestaurantException("Address not found with ID: " + addressId);
					});
			log.debug("Fetched existing address: {}", address);
		}

		restaurant.setAddress(address);

		Optional<Restaurant> existingRestaurant = restDAO.findByRestaurantNameAndAddress(
				restaurant.getRestaurantName(), address
		);

		if (existingRestaurant.isPresent()) {
			log.warn("Duplicate restaurant found with name '{}' and address ID {}", restaurant.getRestaurantName(), address.getAddressId());
			throw new RestaurantException("Restaurant with this name and address already exists.");
		}

		restaurant.setRestaurantId(null); // Let Hibernate assign the ID
		Restaurant savedRestaurant = restDAO.save(restaurant);
		log.info("Restaurant added successfully with ID: {}", savedRestaurant.getRestaurantId());
		return savedRestaurant;
	}

	@Override
	public Restaurant updateRestaurant(Restaurant restaurant) throws RestaurantException {
		log.info("Updating restaurant with ID: {}", restaurant.getRestaurantId());

		Optional<Restaurant> opt = restDAO.findById(restaurant.getRestaurantId());
		if (opt.isPresent()) {
			Restaurant updated = restDAO.save(restaurant);
			log.info("Restaurant updated successfully");
			return updated;
		} else {
			log.error("Restaurant not found with ID: {}", restaurant.getRestaurantId());
			throw new RestaurantException("No such Restaurant exists..");
		}
	}

	@Override
	public Restaurant removeRestaurant(Integer restaurantId) throws RestaurantException {
		log.info("Removing restaurant with ID: {}", restaurantId);

		Optional<Restaurant> opt = restDAO.findById(restaurantId);
		if (opt.isPresent()) {
			Restaurant restaurant = opt.get();
			restDAO.delete(restaurant);
			log.info("Restaurant deleted successfully");
			return restaurant;
		} else {
			log.error("Restaurant not found with ID: {}", restaurantId);
			throw new RestaurantException("No Restaurant found with ID: " + restaurantId);
		}
	}

	@Override
	public Restaurant viewRestaurant(Integer restaurantId) throws RestaurantException {
		log.info("Fetching restaurant with ID: {}", restaurantId);

		Optional<Restaurant> opt = restDAO.findById(restaurantId);
		if (opt.isPresent()) {
			Restaurant restaurant = opt.get();
			log.debug("Restaurant found: {}", restaurant);
			return restaurant;
		} else {
			log.warn("Restaurant not found with ID: {}", restaurantId);
			throw new RestaurantException("No Restaurant found with ID: " + restaurantId);
		}
	}
}
