package com.example.SpringProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.SpringProject.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

}
