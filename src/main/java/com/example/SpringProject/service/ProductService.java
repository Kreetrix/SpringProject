package com.example.SpringProject.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.SpringProject.dao.ProductDto;
import com.example.SpringProject.entity.Product;
import com.example.SpringProject.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductDto getProductById(Integer id) {
        Product product = productRepository.findById(id).orElseThrow();

        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getProductName());
        productDto.setPrice(product.getPrice());

        List<String> suppliers = product.getSuppliers().stream()
                .map(supplier -> supplier.getName())
                .toList();
        productDto.setSupplierNames(suppliers);

        return productDto;
    }
}