package com.webstore.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class AddressDTO {

    @Data
    public static class Request {
        @NotBlank
        private String streetAddress;
        private String postalCode;
        @NotBlank
        private String city;
        private String country;
    }

    @Data
    public static class Response {
        private Integer id;
        private String streetAddress;
        private String postalCode;
        private String city;
        private String country;
    }
}
