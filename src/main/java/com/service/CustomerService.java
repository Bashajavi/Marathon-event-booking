package com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.entity.Customer;
import com.repo.CustomerRepository;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public boolean isEmailExists(String email) {
        int count = customerRepository.countByEmail(email);
        return count > 0;
    }

    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }
}
