package com.webstore.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

public class ProductDTO {

    @Data
    public static class Request {
        @NotBlank
        private String name;
        private String description;
        @NotNull @DecimalMin("0.00")
        private BigDecimal price;
        @Min(0)
        private Integer stockQuantity = 0;
        private Integer categoryId;
        private Integer supplierId;
    }

    @Data
    public static class Response {
        private Integer id;
        private String name;
        private String description;
        private BigDecimal price;
        private Integer stockQuantity;
        private String categoryName;
        private String supplierName;
        private String availability;
    }

    @Data
    public static class StockUpdateRequest {
        @NotNull @Min(0)
        private Integer stockQuantity;
    }
}
