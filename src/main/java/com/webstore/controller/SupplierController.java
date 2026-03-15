package com.webstore.controller;

import com.webstore.dto.AddressDTO;
import com.webstore.dto.SupplierDTO;
import com.webstore.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    public List<SupplierDTO.Response> getAll(
            @RequestParam(required = false) String search) {
        if (search != null && !search.isBlank()) return supplierService.search(search);
        return supplierService.findAll();
    }

    @GetMapping("/{id}")
    public SupplierDTO.DetailResponse getById(@PathVariable Integer id) {
        return supplierService.findById(id);
    }

    @PostMapping
    public ResponseEntity<SupplierDTO.Response> create(@Valid @RequestBody SupplierDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(supplierService.create(req));
    }

    @PutMapping("/{id}")
    public SupplierDTO.Response update(@PathVariable Integer id,
                                       @Valid @RequestBody SupplierDTO.Request req) {
        return supplierService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        supplierService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/addresses")
    public List<AddressDTO.Response> getAddresses(@PathVariable Integer id) {
        return supplierService.findAddresses(id);
    }

    @PostMapping("/{id}/addresses")
    public ResponseEntity<AddressDTO.Response> addAddress(@PathVariable Integer id,
                                                          @Valid @RequestBody AddressDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(supplierService.addAddress(id, req));
    }

    @DeleteMapping("/{id}/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Integer id,
                                              @PathVariable Integer addressId) {
        supplierService.deleteAddress(id, addressId);
        return ResponseEntity.noContent().build();
    }
}
