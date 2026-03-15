package com.webstore.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CustomerStatsDTO {
    private Integer customerId;
    private String customerName;
    private String email;
    private Long totalOrders;
    private BigDecimal totalSpent;
    private LocalDateTime lastOrderDate;
}
