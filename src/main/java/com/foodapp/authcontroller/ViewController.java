package com.foodapp.authcontroller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
//created this for frontend
@Controller
public class ViewController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Returns login.html from templates
    }

    @GetMapping("/signup")
    public String showSignupPage() {
        return "signup"; // Returns signup.html from templates
    }
}
