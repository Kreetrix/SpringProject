package com.example.SpringProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.SpringProject.entity.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, Integer> {
    Supplier findByEmail(String email);
}
