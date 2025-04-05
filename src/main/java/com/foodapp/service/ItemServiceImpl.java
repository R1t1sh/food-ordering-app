//package com.foodapp.service;
//
//import java.util.List;
//import java.util.Optional;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.foodapp.exceptions.ItemException;
//import com.foodapp.model.Item;
//import com.foodapp.repository.ItemDAO;
//
//@Service
//public class ItemServiceImpl implements ItemService{
//
//	@Autowired
//	ItemDAO itemDAO;
//
//
//
//	@Override
//	public Item addItem(Item item) throws ItemException {
//		Optional<Item> opt = itemDAO.findById(item.getItemId());
//		if(opt.isPresent()) {
//			throw new ItemException("Item already exists..");
//		}else {
//			return itemDAO.save(item);
//		}
//	}
//
//
//
//	@Override
//	public Item updateItem(Item item) throws ItemException {
//		Optional<Item> opt = itemDAO.findById(item.getItemId());
//		if(opt.isPresent()) {
//			return itemDAO.save(item);
//		}else {
//			throw new ItemException("No such Item found..");
//		}
//	}
//
//
//
//	@Override
//	public Item viewItem(Integer itemId) throws ItemException {
//		Optional<Item> opt = itemDAO.findById(itemId);
//		if(opt.isPresent()) {
//			return opt.get();
//		}else {
//			throw new ItemException("No Item found with ID: "+itemId);
//		}
//	}
//
//
//
//	@Override
//	public Item removeItem(Integer itemId) throws ItemException {
//		Optional<Item> opt = itemDAO.findById(itemId);
//		if(opt.isPresent()) {
//			Item item = opt.get();
//			itemDAO.delete(item);
//			return item;
//		}else {
//			throw new ItemException("No Item found with ID: "+itemId);
//		}
//	}
//
//
//
//	@Override
//	public List<Item> viewAllItems() throws ItemException {
//		List<Item> items = itemDAO.findAll();
//		if(items.size() > 0) {
//			return items;
//		}else {
//			throw new ItemException("No Item exists..");
//		}
//	}
//
//
//
//}
package com.foodapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodapp.exceptions.ItemException;
import com.foodapp.model.Category;
import com.foodapp.model.Item;
import com.foodapp.repository.CategoryDAO;
import com.foodapp.repository.ItemDAO;

@Service
public class ItemServiceImpl implements ItemService {

	@Autowired
	private ItemDAO itemDAO;

	@Autowired
	private CategoryDAO categoryDAO;

	@Override
	public Item addItem(Item item) throws ItemException {
		// Ensure Category Exists Before Saving Item
		if (item.getCategory() == null || item.getCategory().getCategoryId() == null) {
			throw new ItemException("Category ID must not be null.");
		}

		Category category = categoryDAO.findById(item.getCategory().getCategoryId())
				.orElseThrow(() -> new ItemException("Category not found with ID: " + item.getCategory().getCategoryId()));


		item.setCategory(category);


		return itemDAO.save(item);
	}

	@Override
	public Item updateItem(Item item) throws ItemException {
		if (item.getItemId() == null) {
			throw new ItemException("Item ID must not be null for update.");
		}

		// Ensure Item Exists Before Updating
		Item existingItem = itemDAO.findById(item.getItemId())
				.orElseThrow(() -> new ItemException("No Item found with ID: " + item.getItemId()));

		// Keep the Existing Category
		if (item.getCategory() != null && item.getCategory().getCategoryId() != null) {
			Category category = categoryDAO.findById(item.getCategory().getCategoryId())
					.orElseThrow(() -> new ItemException("Category not found with ID: " + item.getCategory().getCategoryId()));
			existingItem.setCategory(category);
		}

		// Update Other Fields
		existingItem.setItemName(item.getItemName());
		existingItem.setQuantity(item.getQuantity());
		existingItem.setCost(item.getCost());

		return itemDAO.save(existingItem);
	}

	@Override
	public Item viewItem(Integer itemId) throws ItemException {
		return itemDAO.findById(itemId)
				.orElseThrow(() -> new ItemException("No Item found with ID: " + itemId));
	}

	@Override
	public Item removeItem(Integer itemId) throws ItemException {
		Item item = itemDAO.findById(itemId)
				.orElseThrow(() -> new ItemException("No Item found with ID: " + itemId));

		itemDAO.deleteById(itemId);
		return item;
	}

	@Override
	public List<Item> viewAllItems() throws ItemException {
		List<Item> items = itemDAO.findAll();
		if (items.isEmpty()) {
			throw new ItemException("No items exist.");
		}
		return items;
	}
}
