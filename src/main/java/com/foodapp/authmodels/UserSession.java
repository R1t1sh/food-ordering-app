package com.foodapp.authmodels;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
//used Lombok for getters,setters and constructors
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class UserSession {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(unique =  true)
	private Integer userId;
	private String UUID;
	private LocalDateTime timeStamp;
	@Column(nullable = false)
	private String role = "USER";


	public UserSession(Integer userId, String uuid, LocalDateTime localDateTime, String role) {
		super();
		this.userId = userId;
		this.UUID = uuid;
		this.timeStamp = localDateTime;
		this.role= this.role;
	}


}
