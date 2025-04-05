package com.foodapp.service;

import java.util.List;

import com.foodapp.exceptions.CustomerException;
import com.foodapp.exceptions.ItemException;
import com.foodapp.exceptions.OrderException;
import com.foodapp.model.Item;
import com.foodapp.model.OrderDetails;

public interface OrderDetailService {
	
	 OrderDetails addOrder(OrderDetails order)throws OrderException;
	
	 OrderDetails updateOrder(OrderDetails order)throws OrderException;
	
	 OrderDetails removeOrder(Integer orderId)throws OrderException;
	
	 OrderDetails viewOrder(Integer orderId)throws OrderException;
	
     List<Item> viewAllOrdersByCustomer(Integer customerId)throws OrderException,CustomerException;


	 OrderDetails checkoutOrder(Integer cartId) throws OrderException, ItemException, CustomerException;



}
