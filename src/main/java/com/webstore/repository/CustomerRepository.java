package com.webstore.repository;

import com.webstore.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    Optional<Customer> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT c FROM Customer c WHERE LOWER(c.lastName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(c.firstName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Customer> searchByName(@Param("name") String name);
}
