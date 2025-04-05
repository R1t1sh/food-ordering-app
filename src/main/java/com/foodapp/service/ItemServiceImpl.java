package com.foodapp.service;

import java.util.List;
import java.util.Optional;

import com.foodapp.exceptions.ItemException;
import com.foodapp.model.Category;
import com.foodapp.model.Item;
import com.foodapp.repository.CategoryDAO;
import com.foodapp.repository.ItemDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

	@Autowired
	private ItemDAO itemDAO;

	@Autowired
	private CategoryDAO categoryDAO;

	@Override
	public Item addItem(Item item) throws ItemException {
		log.info("Adding new item: {}", item);

		if (item.getCategory() == null || item.getCategory().getCategoryId() == null) {
			log.error("Category ID missing for item");
			throw new ItemException("Category ID must not be null.");
		}

		Category category = categoryDAO.findById(item.getCategory().getCategoryId())
				.orElseThrow(() -> {
					log.error("Category not found with ID: {}", item.getCategory().getCategoryId());
					return new ItemException("Category not found with ID: " + item.getCategory().getCategoryId());
				});

		item.setCategory(category);
		Item savedItem = itemDAO.save(item);
		log.info("Item saved successfully with ID: {}", savedItem.getItemId());

		return savedItem;
	}

	@Override
	public Item updateItem(Item item) throws ItemException {
		log.info("Updating item: {}", item);

		if (item.getItemId() == null) {
			log.error("Item ID is null during update");
			throw new ItemException("Item ID must not be null for update.");
		}

		Item existingItem = itemDAO.findById(item.getItemId())
				.orElseThrow(() -> new ItemException("No Item found with ID: " + item.getItemId()));

		if (item.getCategory() != null && item.getCategory().getCategoryId() != null) {
			Category category = categoryDAO.findById(item.getCategory().getCategoryId())
					.orElseThrow(() -> new ItemException("Category not found with ID: " + item.getCategory().getCategoryId()));
			existingItem.setCategory(category);
		}

		existingItem.setItemName(item.getItemName());
		existingItem.setQuantity(item.getQuantity());
		existingItem.setCost(item.getCost());

		log.debug("Item after update: {}", existingItem);
		return itemDAO.save(existingItem);
	}

	@Override
	public Item viewItem(Integer itemId) throws ItemException {
		log.info("Viewing item with ID: {}", itemId);
		return itemDAO.findById(itemId)
				.orElseThrow(() -> new ItemException("No Item found with ID: " + itemId));
	}

	@Override
	public Item removeItem(Integer itemId) throws ItemException {
		log.info("Removing item with ID: {}", itemId);
		Item item = itemDAO.findById(itemId)
				.orElseThrow(() -> new ItemException("No Item found with ID: " + itemId));
		itemDAO.deleteById(itemId);
		log.info("Item deleted successfully");
		return item;
	}

	@Override
	public List<Item> viewAllItems() throws ItemException {
		log.info("Fetching all items");
		List<Item> items = itemDAO.findAll();
		if (items.isEmpty()) {
			log.warn("No items found in database");
			throw new ItemException("No items exist.");
		}
		log.debug("Found {} items", items.size());
		return items;
	}
}
