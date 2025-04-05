package com.foodapp.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class OrderDetails {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer orderId;

	private LocalDateTime orderDate;

	private String orderStatus;

	@ManyToOne
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Item> items;


	@OneToOne(cascade = CascadeType.ALL,orphanRemoval = true)
	@JsonIgnore
	private  Bill bill;

	public List<Item> getItems() {
		return items;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}



}
