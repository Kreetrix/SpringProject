package com.example.SpringProject.dao;

import java.util.List;

public class ProductDto {
    private Integer id;
    private String name;
    private double price;
    private List<String> supplierNames;

    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public double getPrice() {
        return this.price;
    }

    public List<String> getSupplierNames() {
        return this.supplierNames;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setSupplierNames(List<String> supplierNames) {
        this.supplierNames = supplierNames;
    }
}