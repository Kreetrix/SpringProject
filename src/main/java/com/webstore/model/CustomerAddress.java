package com.webstore.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customeraddresses")
@Getter @Setter @NoArgsConstructor
public class CustomerAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotBlank
    @Column(name = "street_address", nullable = false, length = 255)
    private String streetAddress;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String city;

    @Column(length = 100)
    private String country;
}
