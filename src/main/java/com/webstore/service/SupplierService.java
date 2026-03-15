package com.webstore.service;

import com.webstore.dto.AddressDTO;
import com.webstore.dto.SupplierDTO;
import com.webstore.exception.ResourceNotFoundException;
import com.webstore.model.Product;
import com.webstore.model.Supplier;
import com.webstore.model.SupplierAddress;
import com.webstore.repository.ProductRepository;
import com.webstore.repository.SupplierAddressRepository;
import com.webstore.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepo;
    private final SupplierAddressRepository addressRepo;
    private final ProductRepository productRepo;

    public List<SupplierDTO.Response> findAll() {
        return supplierRepo.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public SupplierDTO.DetailResponse findById(Integer id) {
        Supplier supplier = supplierRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + id));
        return toDetailResponse(supplier);
    }

    public List<SupplierDTO.Response> search(String name) {
        return supplierRepo.findByNameContainingIgnoreCase(name).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public SupplierDTO.Response create(SupplierDTO.Request req) {
        Supplier supplier = new Supplier();
        supplier.setName(req.getName());
        supplier.setContactName(req.getContactName());
        supplier.setPhone(req.getPhone());
        supplier.setEmail(req.getEmail());
        return toResponse(supplierRepo.save(supplier));
    }

    @Transactional
    public SupplierDTO.Response update(Integer id, SupplierDTO.Request req) {
        Supplier supplier = supplierRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + id));
        supplier.setName(req.getName());
        supplier.setContactName(req.getContactName());
        supplier.setPhone(req.getPhone());
        supplier.setEmail(req.getEmail());
        return toResponse(supplierRepo.save(supplier));
    }

    @Transactional
    public void delete(Integer id) {
        if (!supplierRepo.existsById(id)) {
            throw new ResourceNotFoundException("Supplier not found: " + id);
        }
        List<Product> products = productRepo.findBySupplierId(id);
        for (Product product : products) {
            product.setSupplier(null);
            productRepo.save(product);
        }
        supplierRepo.deleteById(id);
    }

    public List<AddressDTO.Response> findAddresses(Integer supplierId) {
        if (!supplierRepo.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier not found: " + supplierId);
        }
        return addressRepo.findBySupplierId(supplierId).stream()
                .map(this::toAddressResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AddressDTO.Response addAddress(Integer supplierId, AddressDTO.Request req) {
        Supplier supplier = supplierRepo.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + supplierId));
        SupplierAddress addr = new SupplierAddress();
        addr.setSupplier(supplier);
        addr.setStreetAddress(req.getStreetAddress());
        addr.setPostalCode(req.getPostalCode());
        addr.setCity(req.getCity());
        addr.setCountry(req.getCountry());
        return toAddressResponse(addressRepo.save(addr));
    }

    @Transactional
    public void deleteAddress(Integer supplierId, Integer addressId) {
        SupplierAddress addr = addressRepo.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found: " + addressId));
        if (!addr.getSupplier().getId().equals(supplierId)) {
            throw new IllegalArgumentException("Address does not belong to supplier " + supplierId);
        }
        addressRepo.delete(addr);
    }

    private SupplierDTO.Response toResponse(Supplier supplier) {
        SupplierDTO.Response r = new SupplierDTO.Response();
        r.setId(supplier.getId());
        r.setName(supplier.getName());
        r.setContactName(supplier.getContactName());
        r.setPhone(supplier.getPhone());
        r.setEmail(supplier.getEmail());
        return r;
    }

    private SupplierDTO.DetailResponse toDetailResponse(Supplier supplier) {
        SupplierDTO.DetailResponse r = new SupplierDTO.DetailResponse();
        r.setId(supplier.getId());
        r.setName(supplier.getName());
        r.setContactName(supplier.getContactName());
        r.setPhone(supplier.getPhone());
        r.setEmail(supplier.getEmail());
        r.setAddresses(addressRepo.findBySupplierId(supplier.getId()).stream()
                .map(this::toAddressResponse)
                .collect(Collectors.toList()));
        return r;
    }

    private AddressDTO.Response toAddressResponse(SupplierAddress addr) {
        AddressDTO.Response r = new AddressDTO.Response();
        r.setId(addr.getId());
        r.setStreetAddress(addr.getStreetAddress());
        r.setPostalCode(addr.getPostalCode());
        r.setCity(addr.getCity());
        r.setCountry(addr.getCountry());
        return r;
    }
}
