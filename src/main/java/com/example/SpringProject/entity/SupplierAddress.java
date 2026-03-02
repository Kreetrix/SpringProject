package com.example.SpringProject.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "supplieraddresses")
public class SupplierAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String street_address;
    private String postal_code;
    private String city;
    private String country;

    @OneToOne
    @JoinColumn(name = "supplier_id", referencedColumnName = "id")
    private Supplier supplier;

    public Integer getId() {
        return this.id;
    }

    public String getStreetAddress() {
        return this.street_address;
    }

    public String getCity() {
        return this.city;
    }

    public String getPostalCode() {
        return this.postal_code;
    }

    public String getCountry() {
        return this.country;
    }

    public void setStreetAddress(String address) {
        this.street_address = address;
    }

    public void setCiry(String city) {
        this.city = city;
    }

    public void setPostalCode(String postalCode) {
        this.postal_code = postalCode;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
