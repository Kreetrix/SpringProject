package com.webstore.repository;

import com.webstore.model.SupplierAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierAddressRepository extends JpaRepository<SupplierAddress, Integer> {
    List<SupplierAddress> findBySupplierId(Integer supplierId);
}
