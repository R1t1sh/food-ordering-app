package com.foodapp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import com.foodapp.model.Customer;
import com.foodapp.repository.CustomerDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodapp.exceptions.CartException;
import com.foodapp.exceptions.ItemException;
import com.foodapp.model.FoodCart;
import com.foodapp.model.Item;
import com.foodapp.repository.FoodCartDAO;
import com.foodapp.repository.ItemDAO;

@Service
public class FoodCartServiceImpl implements FoodCartService{

	@Autowired
	FoodCartDAO cartDAO;

	@Autowired
	ItemDAO itemDAO;
	@Autowired
	CustomerDAO customerDAO;


	@Override
	public FoodCart saveCart(FoodCart cart) throws CartException {
		if (cart == null) {
			throw new CartException("Cart object is null!");
		}

		if (cart.getCustomer() == null) {
			throw new CartException("Customer information is missing in the request!");
		}

		if (cart.getCustomer().getCustomerId() == null) {
			throw new CartException("Customer ID is missing!");
		}

		Optional<Customer> existingCustomer = customerDAO.findById(cart.getCustomer().getCustomerId());

		if (!existingCustomer.isPresent()) {
			throw new CartException("Customer with ID " + cart.getCustomer().getCustomerId() + " not found!");
		}

		cart.setCustomer(existingCustomer.get());


		List<Item> managedItems = new ArrayList<>();
		for (Item item : cart.getItemList()) {
			if (item.getItemId() == null) {
				item = itemDAO.save(item);
			} else {
				Optional<Item> existingItem = itemDAO.findById(item.getItemId());
				if (existingItem.isPresent()) {
					managedItems.add(existingItem.get());
				} else {
					throw new CartException("Item with ID " + item.getItemId() + " not found!");
				}
			}
		}
		cart.setItemList(managedItems);

		return cartDAO.save(cart);
	}


	@Override
	public FoodCart clearCart(Integer cartId) throws CartException {
		Optional<FoodCart> opt = cartDAO.findById(cartId);
		if(opt.isPresent()) {
			FoodCart cart = opt.get();
			cartDAO.delete(cart);
			return cart;
		}else {
			throw new CartException("No Cart found with ID: "+cartId);
		}
	}


	@Override
	public FoodCart viewCart(Integer cartId) throws CartException {
		Optional<FoodCart> opt = cartDAO.findById(cartId);
		if(opt.isPresent()) {
			FoodCart cart = opt.get();
			return cart;
		}else {
			throw new CartException("No Cart found with ID: "+cartId);
		}
	}

@Override
public FoodCart addItem(Integer cartId, Integer itemId) throws CartException, ItemException {
	Optional<FoodCart> cOpt = cartDAO.findById(cartId);
	if (!cOpt.isPresent()) {
		throw new CartException("No Cart found with ID: " + cartId);
	}

	Optional<Item> iOpt = itemDAO.findById(itemId);
	if (!iOpt.isPresent()) {
		throw new ItemException("No Item found with ID: " + itemId);
	}

	FoodCart cart = cOpt.get();
	Item item = iOpt.get();

	// Check if item is already in a different cart
	if (item.getFoodCart() != null && !item.getFoodCart().getCartId().equals(cartId)) {
		throw new ItemException("Item already belongs to another cart.");
	}

	item.setFoodCart(cart);
	itemDAO.save(item);

	if (cart.getItemList() == null) {
		cart.setItemList(new ArrayList<>());
	}

	if (!cart.getItemList().contains(item)) {
		cart.getItemList().add(item);
	}

	return cartDAO.save(cart);
}




}
