package com.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.entity.Customer;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {

    Customer findByEmail(String email);

    @Query("SELECT COUNT(c) FROM Customer c WHERE c.email = ?1")
    int countByEmail(String email);
}
