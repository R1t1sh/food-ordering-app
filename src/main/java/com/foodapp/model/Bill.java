package com.foodapp.model;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Bill {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer billId;
	private LocalDateTime billDate;
	private Double totalCost;
	private Integer totalItem;
	private String status; // "PENDING", "PAID"


	@OneToOne(cascade = CascadeType.ALL)
	private OrderDetails order;

	@OneToOne(mappedBy = "bill", cascade = CascadeType.ALL)
	private Payment payment;

	public Bill(Double totalCost, Integer totalItem, OrderDetails order) {
		this.billDate = LocalDateTime.now();
		this.totalCost = totalCost;
		this.totalItem = totalItem;
		this.order = order;
		this.status = "PENDING";
	}
	
}
