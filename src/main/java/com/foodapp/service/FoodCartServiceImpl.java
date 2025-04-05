package com.foodapp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.foodapp.exceptions.CartException;
import com.foodapp.exceptions.ItemException;
import com.foodapp.model.Customer;
import com.foodapp.model.FoodCart;
import com.foodapp.model.Item;
import com.foodapp.repository.CustomerDAO;
import com.foodapp.repository.FoodCartDAO;
import com.foodapp.repository.ItemDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FoodCartServiceImpl implements FoodCartService {

	@Autowired
	private FoodCartDAO cartDAO;

	@Autowired
	private ItemDAO itemDAO;

	@Autowired
	private CustomerDAO customerDAO;

	@Override
	public FoodCart saveCart(FoodCart cart) throws CartException {
		log.info("Saving cart: {}", cart);

		if (cart == null) throw new CartException("Cart object is null!");
		if (cart.getCustomer() == null) throw new CartException("Customer info missing!");
		if (cart.getCustomer().getCustomerId() == null) throw new CartException("Customer ID is missing!");

		Optional<Customer> existingCustomer = customerDAO.findById(cart.getCustomer().getCustomerId());

		if (!existingCustomer.isPresent()) {
			log.error("Customer with ID {} not found", cart.getCustomer().getCustomerId());
			throw new CartException("Customer with ID " + cart.getCustomer().getCustomerId() + " not found!");
		}

		cart.setCustomer(existingCustomer.get());

		List<Item> managedItems = new ArrayList<>();
		for (Item item : cart.getItemList()) {
			if (item.getItemId() == null) {
				item = itemDAO.save(item);
				log.debug("Saved new item: {}", item);
			} else {
				Optional<Item> existingItem = itemDAO.findById(item.getItemId());
				if (existingItem.isPresent()) {
					managedItems.add(existingItem.get());
				} else {
					log.error("Item not found with ID: {}", item.getItemId());
					throw new CartException("Item with ID " + item.getItemId() + " not found!");
				}
			}
		}

		cart.setItemList(managedItems);
		FoodCart savedCart = cartDAO.save(cart);
		log.info("Cart saved successfully with ID: {}", savedCart.getCartId());
		return savedCart;
	}

	@Override
	public FoodCart clearCart(Integer cartId) throws CartException {
		log.info("Clearing cart with ID: {}", cartId);
		Optional<FoodCart> opt = cartDAO.findById(cartId);

		if (opt.isPresent()) {
			FoodCart cart = opt.get();
			cartDAO.delete(cart);
			log.info("Cart deleted successfully");
			return cart;
		} else {
			log.error("Cart with ID {} not found", cartId);
			throw new CartException("No Cart found with ID: " + cartId);
		}
	}

	@Override
	public FoodCart viewCart(Integer cartId) throws CartException {
		log.info("Viewing cart with ID: {}", cartId);
		Optional<FoodCart> opt = cartDAO.findById(cartId);

		if (opt.isPresent()) {
			log.debug("Cart found: {}", opt.get());
			return opt.get();
		} else {
			log.warn("No cart found with ID: {}", cartId);
			throw new CartException("No Cart found with ID: " + cartId);
		}
	}

	@Override
	public FoodCart addItem(Integer cartId, Integer itemId) throws CartException, ItemException {
		log.info("Adding item ID {} to cart ID {}", itemId, cartId);

		Optional<FoodCart> cOpt = cartDAO.findById(cartId);
		if (!cOpt.isPresent()) {
			log.error("Cart not found with ID: {}", cartId);
			throw new CartException("No Cart found with ID: " + cartId);
		}

		Optional<Item> iOpt = itemDAO.findById(itemId);
		if (!iOpt.isPresent()) {
			log.error("Item not found with ID: {}", itemId);
			throw new ItemException("No Item found with ID: " + itemId);
		}

		FoodCart cart = cOpt.get();
		Item item = iOpt.get();

		if (item.getFoodCart() != null && !item.getFoodCart().getCartId().equals(cartId)) {
			log.error("Item ID {} already belongs to another cart", itemId);
			throw new ItemException("Item already belongs to another cart.");
		}

		item.setFoodCart(cart);
		itemDAO.save(item);

		if (cart.getItemList() == null) {
			cart.setItemList(new ArrayList<>());
		}

		if (!cart.getItemList().contains(item)) {
			cart.getItemList().add(item);
			log.debug("Item added to cart list");
		}

		FoodCart updatedCart = cartDAO.save(cart);
		log.info("Item added and cart updated successfully");
		return updatedCart;
	}
}
