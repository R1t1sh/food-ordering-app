package com.foodapp.controller;

import com.foodapp.exceptions.BillException;
import com.foodapp.exceptions.CustomerException;
import com.foodapp.exceptions.ItemException;
import com.foodapp.model.Bill;
import com.foodapp.service.BillService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bill")
@Slf4j
public class BillServiceController {

	@Autowired
	private BillService billService;

	@PostMapping("/add")
	public ResponseEntity<Bill> generateBill(@RequestBody Bill bill) throws BillException {
		log.info("Received request to generate new bill");
		log.debug("Bill payload: {}", bill);

		Bill myBill = billService.addBill(bill);

		log.info("Bill generated successfully with ID: {}", myBill.getBillId());
		return new ResponseEntity<>(myBill, HttpStatus.CREATED);
	}

	@PutMapping("/update")
	public ResponseEntity<Bill> updateBill(@RequestBody Bill bill) throws BillException {
		log.info("Received request to update bill with ID: {}", bill.getBillId());
		log.debug("Bill payload for update: {}", bill);

		Bill myBill = billService.updateBill(bill);

		log.info("Bill updated successfully");
		return new ResponseEntity<>(myBill, HttpStatus.ACCEPTED);
	}

	@DeleteMapping("/remove/{billId}")
	public ResponseEntity<Bill> removeBill(@PathVariable("billId") Integer billId) throws BillException {
		log.info("Received request to delete bill with ID: {}", billId);

		Bill removedBill = billService.removeBill(billId);

		log.info("Bill deleted successfully");
		return new ResponseEntity<>(removedBill, HttpStatus.OK);
	}

	@GetMapping("/view/{billId}")
	public ResponseEntity<Bill> viewBill(@PathVariable("billId") Integer billId) throws BillException {
		log.info("Fetching bill with ID: {}", billId);

		Bill bill = billService.viewBill(billId);

		log.debug("Fetched bill details: {}", bill);
		return new ResponseEntity<>(bill, HttpStatus.ACCEPTED);
	}

	@GetMapping("/viewtotal/{customerId}")
	public ResponseEntity<String> getTotalByCustomerId(@PathVariable("customerId") Integer customerId)
			throws ItemException, CustomerException {
		log.info("Generating total bill for customer ID: {}", customerId);

		String total = billService.generateTotalBillById(customerId);

		log.debug("Total generated for customer {}: {}", customerId, total);
		return new ResponseEntity<>(total, HttpStatus.OK);
	}
}
