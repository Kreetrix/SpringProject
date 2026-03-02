package com.example.SpringProject.dao;

import com.example.SpringProject.entity.SupplierAddress;

public class SupplierDto {
    private Integer id;
    private String phone;
    private String email;
    private String contact_name;
    private String name;
    private SupplierAddress supplierAddress;

    public Integer getId() {
        return this.id;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getEmail() {
        return this.email;
    }

    public String getName() {
        return this.name;
    }

    public String getContactName() {
        return this.contact_name;
    }

    public SupplierAddress getSupplierAddress() {
        return this.supplierAddress;
    }

    public void setSupplierAddress(SupplierAddress a) {
        this.supplierAddress = a;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setContactName(String contactName) {
        this.contact_name = contactName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
