package com.webstore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {

    @Data
    public static class Request {
        @NotNull
        private Integer customerId;
        private Integer shippingAddressId;
        @NotEmpty
        private List<OrderItemRequest> items;
    }

    @Data
    public static class OrderItemRequest {
        @NotNull
        private Integer productId;
        @NotNull @Min(1)
        private Integer quantity;
    }

    @Data
    public static class Response {
        private Integer id;
        private Integer customerId;
        private String customerName;
        private LocalDateTime orderDate;
        private LocalDateTime deliveryDate;
        private String status;
        private BigDecimal totalAmount;
        private List<OrderItemResponse> items;
    }

    @Data
    public static class OrderItemResponse {
        private Integer productId;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
    }

    @Data
    public static class StatusUpdateRequest {
        @NotNull
        private String status;
    }

    @Data
    public static class SummaryResponse {
        private Integer orderId;
        private String customerName;
        private String customerEmail;
        private LocalDateTime orderDate;
        private LocalDateTime deliveryDate;
        private String status;
        private BigDecimal totalAmount;
        private Long itemCount;
    }
}
