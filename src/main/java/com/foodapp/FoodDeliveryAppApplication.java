package com.foodapp;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;


@SpringBootApplication
@EnableSwagger2
public class FoodDeliveryAppApplication {


	public static void main(String[] args) {
		SpringApplication.run(FoodDeliveryAppApplication.class, args);
	}




}
