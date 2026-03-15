package com.webstore.repository;

import com.webstore.model.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Integer> {
    List<OrderStatusHistory> findByOrderIdOrderByChangedAtAsc(Integer orderId);
}
