package com.webstore.repository;

import com.webstore.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("SELECT DISTINCT o FROM Order o JOIN FETCH o.customer LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.product LEFT JOIN FETCH o.shippingAddress")
    List<Order> findAllWithDetails();

    @Query("SELECT DISTINCT o FROM Order o JOIN FETCH o.customer LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.product LEFT JOIN FETCH o.shippingAddress WHERE o.customer.id = :customerId")
    List<Order> findByCustomerId(@Param("customerId") Integer customerId);

    @Query("SELECT DISTINCT o FROM Order o JOIN FETCH o.customer LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.product LEFT JOIN FETCH o.shippingAddress WHERE o.status = :status")
    List<Order> findByStatus(@Param("status") String status);

    @Query("SELECT DISTINCT o FROM Order o JOIN FETCH o.customer LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.product LEFT JOIN FETCH o.shippingAddress WHERE o.orderDate BETWEEN :from AND :to")
    List<Order> findByDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT DISTINCT o FROM Order o JOIN FETCH o.customer LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.product LEFT JOIN FETCH o.shippingAddress WHERE o.id = :id")
    Optional<Order> findByIdWithDetails(@Param("id") Integer id);

    @Query("SELECT DISTINCT o FROM Order o JOIN FETCH o.customer LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.product WHERE i.product.id = :productId")
    List<Order> findByProductId(@Param("productId") Integer productId);
}
