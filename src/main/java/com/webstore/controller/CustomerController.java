package com.webstore.controller;

import com.webstore.dto.AddressDTO;
import com.webstore.dto.CustomerDTO;
import com.webstore.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public List<CustomerDTO.Response> getAll(
            @RequestParam(required = false) String search) {
        if (search != null && !search.isBlank()) return customerService.search(search);
        return customerService.findAll();
    }

    @GetMapping("/{id}")
    public CustomerDTO.DetailResponse getById(@PathVariable Integer id) {
        return customerService.findById(id);
    }

    @PostMapping
    public ResponseEntity<CustomerDTO.Response> create(@Valid @RequestBody CustomerDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.create(req));
    }

    @PutMapping("/{id}")
    public CustomerDTO.Response update(@PathVariable Integer id,
                                       @Valid @RequestBody CustomerDTO.Request req) {
        return customerService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/addresses")
    public List<AddressDTO.Response> getAddresses(@PathVariable Integer id) {
        return customerService.findAddresses(id);
    }

    @PostMapping("/{id}/addresses")
    public ResponseEntity<AddressDTO.Response> addAddress(@PathVariable Integer id,
                                                          @Valid @RequestBody AddressDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.addAddress(id, req));
    }

    @PutMapping("/{id}/addresses/{addressId}")
    public AddressDTO.Response updateAddress(@PathVariable Integer id,
                                             @PathVariable Integer addressId,
                                             @Valid @RequestBody AddressDTO.Request req) {
        return customerService.updateAddress(id, addressId, req);
    }

    @DeleteMapping("/{id}/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Integer id,
                                              @PathVariable Integer addressId) {
        customerService.deleteAddress(id, addressId);
        return ResponseEntity.noContent().build();
    }
}
