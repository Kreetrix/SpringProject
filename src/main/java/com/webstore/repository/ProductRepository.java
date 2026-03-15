package com.webstore.repository;

import com.webstore.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.supplier WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Product> findByNameContainingIgnoreCase(@Param("name") String name);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.supplier WHERE p.category.id = :categoryId")
    List<Product> findByCategoryId(@Param("categoryId") Integer categoryId);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.supplier WHERE p.supplier.id = :supplierId")
    List<Product> findBySupplierId(@Param("supplierId") Integer supplierId);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.supplier WHERE p.price BETWEEN :min AND :max")
    List<Product> findByPriceRange(@Param("min") BigDecimal min, @Param("max") BigDecimal max);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.supplier WHERE p.stockQuantity < :threshold")
    List<Product> findLowStock(@Param("threshold") Integer threshold);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.supplier WHERE p.stockQuantity = :quantity")
    List<Product> findByStockQuantityEquals(@Param("quantity") Integer quantity);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.supplier WHERE p.stockQuantity > :quantity")
    List<Product> findByStockQuantityGreaterThan(@Param("quantity") Integer quantity);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.supplier WHERE p.id = :id")
    Optional<Product> findByIdWithDetails(@Param("id") Integer id);
}
