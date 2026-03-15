package com.webstore.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

public class SupplierDTO {

    @Data
    public static class Request {
        @NotBlank
        private String name;
        private String contactName;
        private String phone;
        private String email;
    }

    @Data
    public static class Response {
        private Integer id;
        private String name;
        private String contactName;
        private String phone;
        private String email;
    }

    @Data
    public static class DetailResponse {
        private Integer id;
        private String name;
        private String contactName;
        private String phone;
        private String email;
        private List<AddressDTO.Response> addresses;
    }
}
