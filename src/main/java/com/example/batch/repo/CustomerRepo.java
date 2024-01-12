package com.example.batch.repo;

import com.example.batch.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepo
        extends JpaRepository<Customer,Integer> {
}
