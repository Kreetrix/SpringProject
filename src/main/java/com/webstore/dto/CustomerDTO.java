package com.webstore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class CustomerDTO {

    @Data
    public static class Request {
        @NotBlank
        private String firstName;
        @NotBlank
        private String lastName;
        @NotBlank @Email
        private String email;
        private String phone;
    }

    @Data
    public static class Response {
        private Integer id;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
    }

    @Data
    public static class DetailResponse {
        private Integer id;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private java.util.List<AddressDTO.Response> addresses;
    }
}
