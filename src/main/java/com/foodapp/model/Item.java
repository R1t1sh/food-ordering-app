package com.foodapp.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Item{
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer itemId;
	private String itemName;
	private Integer quantity;
	private Double cost;

	@ManyToOne
	@JoinColumn(name = "order_id")
	private OrderDetails order;

	@OneToOne(cascade = CascadeType.ALL)
	private Category category;

	@ManyToOne
	@JoinColumn(name = "cart_id", nullable = true)
	@JsonBackReference
	private FoodCart foodCart;

	public void setOrder(OrderDetails order) {
		this.order = order;
	}


}