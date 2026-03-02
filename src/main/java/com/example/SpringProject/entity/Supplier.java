package com.example.SpringProject.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "suppliers")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String contact_name;
    private String phone;
    private String email;

    @ManyToMany(mappedBy = "suppliers")
    private List<Product> products = new ArrayList<Product>();

    @OneToOne(mappedBy = "supplier", cascade = CascadeType.ALL)
    private SupplierAddress supplierAddress;

    public Supplier() {

    }

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
}
