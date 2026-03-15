package com.webstore.service;

import com.webstore.dto.ProductCategoryDTO;
import com.webstore.dto.ProductDTO;
import com.webstore.exception.ResourceNotFoundException;
import com.webstore.model.Order;
import com.webstore.model.OrderItem;
import com.webstore.model.Product;
import com.webstore.model.ProductCategory;
import com.webstore.repository.OrderRepository;
import com.webstore.repository.ProductCategoryRepository;
import com.webstore.repository.ProductRepository;
import com.webstore.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepo;
    private final ProductCategoryRepository categoryRepo;
    private final SupplierRepository supplierRepo;
    private final OrderRepository orderRepo;

    public List<ProductDTO.Response> findAll() {
        return productRepo.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProductDTO.Response findById(Integer id) {
        return toResponse(productRepo.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id)));
    }

    public List<ProductDTO.Response> search(String name) {
        return productRepo.findByNameContainingIgnoreCase(name).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ProductDTO.Response> findByCategory(Integer categoryId) {
        return productRepo.findByCategoryId(categoryId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ProductDTO.Response> findBySupplier(Integer supplierId) {
        return productRepo.findBySupplierId(supplierId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ProductDTO.Response> findByPriceRange(BigDecimal min, BigDecimal max) {
        return productRepo.findByPriceRange(min, max).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ProductDTO.Response> findLowStock(Integer threshold) {
        return productRepo.findLowStock(threshold).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ProductDTO.Response> findOutOfStock() {
        return productRepo.findByStockQuantityEquals(0).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductDTO.Response create(ProductDTO.Request req) {
        return toResponse(productRepo.save(applyFields(new Product(), req)));
    }

    @Transactional
    public ProductDTO.Response update(Integer id, ProductDTO.Request req) {
        Product product = productRepo.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        return toResponse(productRepo.save(applyFields(product, req)));
    }

    @Transactional
    public ProductDTO.Response updateStock(Integer id, ProductDTO.StockUpdateRequest req) {
        Product product = productRepo.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        product.setStockQuantity(req.getStockQuantity());
        return toResponse(productRepo.save(product));
    }

    @Transactional
    public void delete(Integer id) {
        if (!productRepo.existsById(id)) {
            throw new ResourceNotFoundException("Product not found: " + id);
        }
        List<Order> affectedOrders = orderRepo.findByProductId(id);
        for (Order order : affectedOrders) {
            order.getItems().removeIf(item -> item.getProduct().getId().equals(id));
            orderRepo.save(order);
        }
        productRepo.deleteById(id);
    }

    public List<ProductCategoryDTO.Response> findAllCategories() {
        return categoryRepo.findAll().stream()
                .map(this::toCategoryResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductCategoryDTO.Response createCategory(ProductCategoryDTO.Request req) {
        ProductCategory category = new ProductCategory();
        category.setName(req.getName());
        category.setDescription(req.getDescription());
        return toCategoryResponse(categoryRepo.save(category));
    }

    @Transactional
    public ProductCategoryDTO.Response updateCategory(Integer id, ProductCategoryDTO.Request req) {
        ProductCategory category = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
        category.setName(req.getName());
        category.setDescription(req.getDescription());
        return toCategoryResponse(categoryRepo.save(category));
    }

    @Transactional
    public void deleteCategory(Integer id) {
        if (!categoryRepo.existsById(id)) {
            throw new ResourceNotFoundException("Category not found: " + id);
        }
        List<Product> products = productRepo.findByCategoryId(id);
        for (Product product : products) {
            product.setCategory(null);
            productRepo.save(product);
        }
        categoryRepo.deleteById(id);
    }

    private Product applyFields(Product product, ProductDTO.Request req) {
        product.setName(req.getName());
        product.setDescription(req.getDescription());
        product.setPrice(req.getPrice());
        product.setStockQuantity(req.getStockQuantity() != null ? req.getStockQuantity() : 0);
        product.setCategory(req.getCategoryId() != null
                ? categoryRepo.findById(req.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + req.getCategoryId()))
                : null);
        product.setSupplier(req.getSupplierId() != null
                ? supplierRepo.findById(req.getSupplierId())
                        .orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + req.getSupplierId()))
                : null);
        return product;
    }

    public ProductDTO.Response toResponse(Product product) {
        ProductDTO.Response r = new ProductDTO.Response();
        r.setId(product.getId());
        r.setName(product.getName());
        r.setDescription(product.getDescription());
        r.setPrice(product.getPrice());
        r.setStockQuantity(product.getStockQuantity());
        if (product.getCategory() != null) r.setCategoryName(product.getCategory().getName());
        if (product.getSupplier() != null) r.setSupplierName(product.getSupplier().getName());
        int stock = product.getStockQuantity();
        r.setAvailability(stock == 0 ? "OUT_OF_STOCK" : stock < 10 ? "LOW_STOCK" : "IN_STOCK");
        return r;
    }

    private ProductCategoryDTO.Response toCategoryResponse(ProductCategory category) {
        ProductCategoryDTO.Response r = new ProductCategoryDTO.Response();
        r.setId(category.getId());
        r.setName(category.getName());
        r.setDescription(category.getDescription());
        return r;
    }
}
