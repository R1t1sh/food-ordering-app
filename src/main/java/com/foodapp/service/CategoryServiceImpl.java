package com.foodapp.service;

import java.util.List;
import java.util.Optional;

import com.foodapp.model.Restaurant;
import com.foodapp.repository.RestaurantDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodapp.exceptions.CategoryException;
import com.foodapp.model.Category;
import com.foodapp.repository.CategoryDAO;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryDAO categoryDAO;

	@Autowired
	private RestaurantDAO restaurantDAO;

	@Override
	public Category addCategory(Category category) throws CategoryException {
		log.debug("Attempting to add category: {}", category);

		if (category.getCategoryId() == null) {
			log.error("Failed to add category - ID is null");
			throw new CategoryException("Category ID must not be null.");
		}

		Optional<Category> opt = categoryDAO.findById(category.getCategoryId());
		if (opt.isPresent()) {
			log.error("Category already exists with ID: {}", category.getCategoryId());
			throw new CategoryException("Category already exists..");
		} else {
			if (category.getRestaurant() != null && category.getRestaurant().getRestaurantId() != null) {
				Restaurant restaurant = restaurantDAO.findById(category.getRestaurant().getRestaurantId())
						.orElseThrow(() -> {
							log.error("Restaurant not found with ID: {}", category.getRestaurant().getRestaurantId());
							return new CategoryException("Restaurant not found with ID: " + category.getRestaurant().getRestaurantId());
						});

				category.setRestaurant(restaurant);
				log.debug("Associated restaurant found and set: {}", restaurant);
			}

			Category savedCategory = categoryDAO.save(category);
			log.info("Category successfully added: {}", savedCategory);
			return savedCategory;
		}
	}

	@Override
	public Category updateCategory(Category category) throws CategoryException {
		log.debug("Attempting to update category: {}", category);
		Optional<Category> opt = categoryDAO.findById(category.getCategoryId());

		if (opt.isPresent()) {
			Category updated = categoryDAO.save(category);
			log.info("Category updated successfully: {}", updated);
			return updated;
		} else {
			log.error("Category not found for update with ID: {}", category.getCategoryId());
			throw new CategoryException("No such Category found..");
		}
	}

	@Override
	public Category viewCategory(Integer categoryId) throws CategoryException {
		log.debug("Fetching category with ID: {}", categoryId);
		Optional<Category> opt = categoryDAO.findById(categoryId);

		if (opt.isPresent()) {
			log.info("Category found: {}", opt.get());
			return opt.get();
		} else {
			log.error("No category found with ID: {}", categoryId);
			throw new CategoryException("No Category found with ID: " + categoryId);
		}
	}

	@Override
	public Category removeCategory(Integer categoryId) throws CategoryException {
		log.debug("Attempting to remove category with ID: {}", categoryId);
		Optional<Category> opt = categoryDAO.findById(categoryId);

		if (opt.isPresent()) {
			Category cat = opt.get();
			categoryDAO.delete(cat);
			log.info("Category removed successfully: {}", cat);
			return cat;
		} else {
			log.error("Failed to remove category - ID not found: {}", categoryId);
			throw new CategoryException("No Category found with ID: " + categoryId);
		}
	}

	@Override
	public List<Category> viewAllCategory() throws CategoryException {
		log.debug("Fetching all categories");
		List<Category> categories = categoryDAO.findAll();

		if (!categories.isEmpty()) {
			log.info("Found {} categories", categories.size());
			return categories;
		} else {
			log.error("No categories found");
			throw new CategoryException("No Categories exists..");
		}
	}
}
