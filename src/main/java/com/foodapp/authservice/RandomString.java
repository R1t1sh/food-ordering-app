package com.foodapp.authservice;

import java.util.Random;

public class RandomString {
	//created this for random session key  generation
	public static String getRandomString() {
	   
	    Random rn = new Random();
	    int number = rn.nextInt(999999);

	    return String.format("%06d", number);
	}

}
