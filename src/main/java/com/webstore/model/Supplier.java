package com.webstore.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "suppliers")
@Getter @Setter @NoArgsConstructor
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotBlank
    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "contact_name", length = 100)
    private String contactName;

    @Column(length = 30)
    private String phone;

    @Column(length = 255)
    private String email;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupplierAddress> addresses = new ArrayList<>();
}
