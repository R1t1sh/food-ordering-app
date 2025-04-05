package com.foodapp.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodapp.exceptions.RestaurantException;
import com.foodapp.model.Restaurant;
import com.foodapp.repository.RestaurantDAO;
import com.foodapp.model.Address;
import com.foodapp.repository.AddressDAO;

@Service
public class RestaurantServiceImpl implements RestaurantService{
	
	@Autowired
	RestaurantDAO restDAO;
	@Autowired
	private AddressDAO addressDAO;

@Override
public Restaurant addRestaurant(Restaurant restaurant) throws RestaurantException {
	if (restaurant.getAddress() == null) {
		throw new RestaurantException("Address is required to add a restaurant.");
	}

	Address address;

	if (restaurant.getAddress().getAddressId() == null) {
		// If addressId is null, save it as a new address
		address = addressDAO.save(restaurant.getAddress());
	} else {
		// Fetch the existing address from the database
		Integer addressId = restaurant.getAddress().getAddressId();
		address = addressDAO.findById(addressId)
				.orElseThrow(() -> new RestaurantException("Address not found with ID: " + addressId));
	}

	restaurant.setAddress(address);

	//  if the restaurant already exists with the same name and address
	Optional<Restaurant> existingRestaurant = restDAO.findByRestaurantNameAndAddress(
			restaurant.getRestaurantName(), address
	);

	if (existingRestaurant.isPresent()) {
		throw new RestaurantException("Restaurant with this name and address already exists.");
	}

	restaurant.setRestaurantId(null);
	return restDAO.save(restaurant);
}


	@Override
	public Restaurant updateRestaurant(Restaurant restaurant) throws RestaurantException {
		Optional<Restaurant> opt = restDAO.findById(restaurant.getRestaurantId());
		if(opt.isPresent()) {
			return restDAO.save(restaurant);
		}else {
			throw new RestaurantException("No such Restaurant exists..");
		}
	}



	@Override
	public Restaurant removeRestaurant(Integer restaurantId) throws RestaurantException {
		Optional<Restaurant> opt = restDAO.findById(restaurantId);
		if(opt.isPresent()) {
			Restaurant restaurant = opt.get();
			restDAO.delete(restaurant);
			return restaurant;
		}else {
			throw new RestaurantException("No Restaurant found with ID: "+restaurantId);
		}
	}



	@Override
	public Restaurant viewRestaurant(Integer restaurantId) throws RestaurantException {
		Optional<Restaurant> opt = restDAO.findById(restaurantId);
		if(opt.isPresent()) {
			Restaurant restaurant = opt.get();
			return restaurant;
		}else {
			throw new RestaurantException("No Restaurant found with ID: "+restaurantId);
		}
	}

}
