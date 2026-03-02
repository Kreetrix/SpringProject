package com.example.SpringProject.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.SpringProject.entity.Category;
import com.example.SpringProject.entity.Product;
import com.example.SpringProject.repository.CategoryRepository;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryRepository repository;

    public CategoryController(CategoryRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {

        if (category.getProducts() != null) {
            for (Product p : category.getProducts()) {
                p.setCategory(category);
            }
        }

        Category savedCategory = repository.save(category);

        return ResponseEntity.ok(savedCategory);
    }
}