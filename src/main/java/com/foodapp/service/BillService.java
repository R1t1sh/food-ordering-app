package com.foodapp.service;

import com.foodapp.exceptions.BillException;
import com.foodapp.exceptions.CustomerException;
import com.foodapp.exceptions.ItemException;
import com.foodapp.model.Bill;

 public interface BillService {
	
	 Bill addBill(Bill bill) throws BillException;
	
	 Bill updateBill(Bill bill)throws BillException;
	
	 Bill removeBill(Integer billId)throws BillException;
	
	 Bill viewBill(Integer billId)throws BillException;
	
	 String generateTotalBillById(Integer customerId)throws ItemException,CustomerException;
	

}
