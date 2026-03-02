package com.example.SpringProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.SpringProject.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

}
