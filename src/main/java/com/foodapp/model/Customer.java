package com.foodapp.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Customer {
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer customerId;

	@JsonProperty("fullName")
	private String fullName;

	@JsonProperty("age")
	private Integer age;

	@JsonProperty("gender")
	private String gender;

	@JsonProperty("mobileNumber")
	private String mobileNumber;

	@JsonProperty("email")
	private String email;

	
	@OneToOne(cascade = CascadeType.ALL)
	@JsonProperty("address")
	private Address address;
	
	
	@OneToOne(cascade = CascadeType.ALL)
	@JsonIgnore
	private FoodCart foodCart;



}

