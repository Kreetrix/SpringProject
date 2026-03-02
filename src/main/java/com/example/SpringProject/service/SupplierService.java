package com.example.SpringProject.service;

import org.springframework.stereotype.Service;

import com.example.SpringProject.dao.SupplierDto;
import com.example.SpringProject.entity.Supplier;
import com.example.SpringProject.entity.SupplierAddress;
import com.example.SpringProject.repository.SupplierRepository;

@Service
public class SupplierService {
    private final SupplierRepository repository;

    public SupplierService(SupplierRepository repository) {
        this.repository = repository;
    }

    public SupplierDto getSupplierByEmail(String email) {
        Supplier supplier = repository.findByEmail(email);

        SupplierDto supplierDto = new SupplierDto();
        supplierDto.setEmail(supplier.getEmail());
        supplierDto.setName(supplier.getName());
        supplierDto.setPhone(supplier.getPhone());
        supplierDto.setContactName(supplier.getContactName());

        SupplierAddress supplierAddress = supplier.getSupplierAddress();

        supplierDto.setSupplierAddress(supplierAddress);

        return supplierDto;
    }
}
