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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodapp.exceptions.CustomerException;
import com.foodapp.exceptions.OrderException;
import com.foodapp.repository.CustomerDAO;
import com.foodapp.repository.OrderDAO;

import javax.transaction.Transactional;

@Service
public class OrderDetailServiceImpl implements OrderDetailService{
	
	
	@Autowired
	OrderDAO orderDAO;
	
	@Autowired
	CustomerDAO cusDAO;
	@Autowired
	FoodCartDAO cartDAO;
	@Autowired
	BillDAO billDAO;

	
	
	@Override
	public OrderDetails addOrder(OrderDetails order) throws OrderException {
		Optional<OrderDetails> opt = orderDAO.findById(order.getOrderId());
		if (order.getOrderStatus() == null) {
			order.setOrderStatus("PENDING");
		}

		if(opt.isPresent()) {
			throw new OrderException("Order already exists..");
		}else {
			return orderDAO.save(order);
		}
	}



	@Override
	public OrderDetails updateOrder(OrderDetails order) throws OrderException {
		Optional<OrderDetails> opt = orderDAO.findById(order.getOrderId());
		if(opt.isPresent()) {
			return orderDAO.save(order);
		}else {
			throw new OrderException("Order such Order exists..");
		}
	}



	@Override
	public OrderDetails removeOrder(Integer orderId) throws OrderException {
		Optional<OrderDetails> opt = orderDAO.findById(orderId);
		if(opt.isPresent()) {
			OrderDetails order = opt.get();
			orderDAO.delete(order);
			return order;
		}else {
			throw new OrderException("No Order found with ID: "+orderId);
		}
	}



	@Override
	public OrderDetails viewOrder(Integer orderId) throws OrderException {
		Optional<OrderDetails> opt = orderDAO.findById(orderId);
		if(opt.isPresent()) {
			OrderDetails order = opt.get();
			return order;
		}else {
			throw new OrderException("No Order found with ID: "+orderId);
		}
	}



	@Override
	public List<Item> viewAllOrdersByCustomer(Integer customerId) throws OrderException, CustomerException {
		Optional<Customer> cOpt =cusDAO.findById(customerId);
		if(cOpt.isPresent()) {
			Customer customer = cOpt.get();
			 List<Item> itemList = customer.getFoodCart().getItemList();
			 if(itemList.size() > 0) {
				 return itemList;
			 }else {
				 throw new OrderException("No Orders found..");
			 }
		}else {
			throw new CustomerException("No Customer found with ID: "+customerId);
		}
	}


	@Transactional
	public OrderDetails checkoutOrder(Integer cartId) throws OrderException, ItemException,CustomerException {
		Optional<FoodCart> cartOpt = cartDAO.findById(cartId);
		if (!cartOpt.isPresent() || cartOpt.get().getItemList().isEmpty()) {
			throw new ItemException("Cart is empty or does not exist.");
		}
		FoodCart cart = cartOpt.get();

		OrderDetails order = new OrderDetails();
		Optional<Customer> customerOpt = cusDAO.findById(cart.getCustomer().getCustomerId());
		if (!customerOpt.isPresent()) {
			throw new CustomerException("Customer not found with ID: " + cart.getCustomer().getCustomerId());
		}
		Customer customer = customerOpt.get();

		order.setCustomer(customer);

		order.setOrderDate(LocalDateTime.now());
		order.setOrderStatus("CONFIRMED");

		List<Item> items = cart.getItemList();


		for (Item item : items) {
			item.setOrder(order);
			item.setFoodCart(null); // Detach from cart
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

		cart.getItemList().clear(); // Clear cart after order placement
		cartDAO.save(cart);

		return savedOrder;
	}

}
