package com.webstore.repository;

import com.webstore.model.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerAddressRepository extends JpaRepository<CustomerAddress, Integer> {
    List<CustomerAddress> findByCustomerId(Integer customerId);
}
