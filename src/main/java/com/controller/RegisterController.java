package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.entity.Customer;
import com.service.CustomerService;

@Controller
public class RegisterController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // GET Mapping to display the registration form
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistrationForm(@ModelAttribute("customer") Customer customer, Model model) {
        String email = customer.getEmail();

        // Check if email already exists
        if (customerService.isEmailExists(email)) {
            model.addAttribute("errorMessage", "Email already exists. Please use a different email.");
            return "register";
        }

        // Hash the password before saving
        String hashedPassword = passwordEncoder.encode(customer.getPwd());
        customer.setPwd(hashedPassword);

        // If email doesn't exist, proceed with registration
        customerService.saveCustomer(customer);
        model.addAttribute("message", "Registration successful!");

        return "register";
    }
}
