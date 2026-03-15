package com.webstore.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class ProductCategoryDTO {

    @Data
    public static class Request {
        @NotBlank
        private String name;
        private String description;
    }

    @Data
    public static class Response {
        private Integer id;
        private String name;
        private String description;
    }
}
