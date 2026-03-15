package com.webstore.controller;

import com.webstore.dto.ProductCategoryDTO;
import com.webstore.dto.ProductDTO;
import com.webstore.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<ProductDTO.Response> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer supplierId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {

        if (search != null && !search.isBlank()) return productService.search(search);
        if (categoryId != null) return productService.findByCategory(categoryId);
        if (supplierId != null) return productService.findBySupplier(supplierId);
        if (minPrice != null && maxPrice != null) return productService.findByPriceRange(minPrice, maxPrice);
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public ProductDTO.Response getById(@PathVariable Integer id) {
        return productService.findById(id);
    }

    @GetMapping("/low-stock")
    public List<ProductDTO.Response> getLowStock(
            @RequestParam(defaultValue = "10") Integer threshold) {
        return productService.findLowStock(threshold);
    }

    @GetMapping("/out-of-stock")
    public List<ProductDTO.Response> getOutOfStock() {
        return productService.findOutOfStock();
    }

    @PostMapping
    public ResponseEntity<ProductDTO.Response> create(@Valid @RequestBody ProductDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(req));
    }

    @PutMapping("/{id}")
    public ProductDTO.Response update(@PathVariable Integer id,
                                      @Valid @RequestBody ProductDTO.Request req) {
        return productService.update(id, req);
    }

    @PatchMapping("/{id}/stock")
    public ProductDTO.Response updateStock(@PathVariable Integer id,
                                           @Valid @RequestBody ProductDTO.StockUpdateRequest req) {
        return productService.updateStock(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categories")
    public List<ProductCategoryDTO.Response> getCategories() {
        return productService.findAllCategories();
    }

    @PostMapping("/categories")
    public ResponseEntity<ProductCategoryDTO.Response> createCategory(
            @Valid @RequestBody ProductCategoryDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createCategory(req));
    }

    @PutMapping("/categories/{id}")
    public ProductCategoryDTO.Response updateCategory(@PathVariable Integer id,
                                                      @Valid @RequestBody ProductCategoryDTO.Request req) {
        return productService.updateCategory(id, req);
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        productService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
