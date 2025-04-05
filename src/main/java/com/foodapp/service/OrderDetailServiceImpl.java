package com.foodapp.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.foodapp.exceptions.ItemException;
import com.foodapp.model.*;
import com.foodapp.repository.BillDAO;
import com.foodapp.repository.FoodCartDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodapp.exceptions.CustomerException;
import com.foodapp.exceptions.OrderException;
import com.foodapp.repository.CustomerDAO;
import com.foodapp.repository.OrderDAO;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
@Service
@Slf4j
public class OrderDetailServiceImpl implements OrderDetailService {

	@Autowired
	private OrderDAO orderDAO;

	@Autowired
	private CustomerDAO cusDAO;

	@Autowired
	private FoodCartDAO cartDAO;

	@Autowired
	private BillDAO billDAO;

	@PersistenceContext
	private EntityManager em;

	@Override
	public OrderDetails addOrder(OrderDetails order) throws OrderException {
		log.info("Adding new order: {}", order);

		Optional<OrderDetails> opt = orderDAO.findById(order.getOrderId());
		if (order.getOrderStatus() == null) order.setOrderStatus("PENDING");

		if (opt.isPresent()) {
			log.error("Order already exists with ID: {}", order.getOrderId());
			throw new OrderException("Order already exists..");
		}

		OrderDetails savedOrder = orderDAO.save(order);
		log.info("Order created with ID: {}", savedOrder.getOrderId());
		return savedOrder;
	}

	@Override
	public OrderDetails updateOrder(OrderDetails order) throws OrderException {
		log.info("Updating order ID: {}", order.getOrderId());

		Optional<OrderDetails> opt = orderDAO.findById(order.getOrderId());
		if (opt.isPresent()) {
			OrderDetails updated = orderDAO.save(order);
			log.info("Order updated successfully");
			return updated;
		} else {
			log.error("No order found with ID: {}", order.getOrderId());
			throw new OrderException("No such Order exists..");
		}
	}

	@Transactional
	@Override
	public OrderDetails removeOrder(Integer orderId) throws OrderException {
		log.info("Removing order ID: {}", orderId);
		Optional<OrderDetails> opt = orderDAO.findById(orderId);
		if (opt.isPresent()) {
			OrderDetails order = opt.get();

			if (order.getItems() != null) {
				order.getItems().forEach(item -> item.setOrder(null));
				order.getItems().clear();
			}

			if (order.getBill() != null) {
				Bill bill = order.getBill();
				bill.setOrder(null);
				order.setBill(null);
				billDAO.save(bill);
			}

			orderDAO.save(order);
			em.flush();

			if (order.getBill() != null) {
				billDAO.delete(order.getBill());
			}

			orderDAO.delete(order);
			log.info("Order and related bill deleted successfully");
			return order;
		} else {
			log.error("Order not found with ID: {}", orderId);
			throw new OrderException("No Order found with ID: " + orderId);
		}
	}

	@Override
	public OrderDetails viewOrder(Integer orderId) throws OrderException {
		log.info("Fetching order ID: {}", orderId);
		return orderDAO.findById(orderId)
				.orElseThrow(() -> new OrderException("No Order found with ID: " + orderId));
	}

	@Override
	public List<Item> viewAllOrdersByCustomer(Integer customerId) throws OrderException, CustomerException {
		log.info("Fetching orders for customer ID: {}", customerId);
		Optional<Customer> cOpt = cusDAO.findById(customerId);

		if (cOpt.isPresent()) {
			List<Item> itemList = cOpt.get().getFoodCart().getItemList();
			if (itemList.isEmpty()) {
				log.warn("No orders found for customer ID: {}", customerId);
				throw new OrderException("No Orders found..");
			}
			log.debug("Found {} items for customer ID {}", itemList.size(), customerId);
			return itemList;
		} else {
			log.error("Customer not found with ID: {}", customerId);
			throw new CustomerException("No Customer found with ID: " + customerId);
		}
	}

	@Transactional
	public OrderDetails checkoutOrder(Integer cartId) throws OrderException, ItemException, CustomerException {
		log.info("Checking out cart ID: {}", cartId);
		Optional<FoodCart> cartOpt = cartDAO.findById(cartId);

		if (!cartOpt.isPresent() || cartOpt.get().getItemList().isEmpty()) {
			log.error("Cart not found or empty with ID: {}", cartId);
			throw new ItemException("Cart is empty or does not exist.");
		}

		FoodCart cart = cartOpt.get();
		Optional<Customer> customerOpt = cusDAO.findById(cart.getCustomer().getCustomerId());

		if (!customerOpt.isPresent()) {
			log.error("Customer not found during checkout for cart ID: {}", cartId);
			throw new CustomerException("Customer not found with ID: " + cart.getCustomer().getCustomerId());
		}

		Customer customer = customerOpt.get();
		OrderDetails order = new OrderDetails();
		order.setCustomer(customer);
		order.setOrderDate(LocalDateTime.now());
		order.setOrderStatus("CONFIRMED");

		List<Item> items = cart.getItemList();
		for (Item item : items) {
			item.setOrder(order);
			item.setFoodCart(null);
		}

		order.setItems(new ArrayList<>(items));

		double totalAmount = items.stream().mapToDouble(item -> item.getCost() * item.getQuantity()).sum();
		int totalItems = items.stream().mapToInt(Item::getQuantity).sum();

		Bill bill = new Bill();
		bill.setTotalCost(totalAmount);
		bill.setTotalItem(totalItems);
		bill.setOrder(order);
		bill.setBillDate(LocalDateTime.now());
		bill.setStatus("PENDING");

		order.setBill(bill);

		OrderDetails savedOrder = orderDAO.save(order);
		log.info("Order placed successfully with ID: {}", savedOrder.getOrderId());

		cart.getItemList().clear();
		cartDAO.save(cart);

		return savedOrder;
	}
}