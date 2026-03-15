package com.webstore.service;

import com.webstore.dto.AddressDTO;
import com.webstore.dto.CustomerDTO;
import com.webstore.exception.ResourceNotFoundException;
import com.webstore.model.Customer;
import com.webstore.model.CustomerAddress;
import com.webstore.model.Order;
import com.webstore.repository.CustomerAddressRepository;
import com.webstore.repository.CustomerRepository;
import com.webstore.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepo;
    private final CustomerAddressRepository addressRepo;
    private final OrderRepository orderRepo;

    public List<CustomerDTO.Response> findAll() {
        return customerRepo.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CustomerDTO.DetailResponse findById(Integer id) {
        Customer customer = customerRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
        return toDetailResponse(customer);
    }

    public List<CustomerDTO.Response> search(String name) {
        return customerRepo.searchByName(name).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CustomerDTO.Response create(CustomerDTO.Request req) {
        if (customerRepo.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + req.getEmail());
        }
        Customer customer = new Customer();
        customer.setFirstName(req.getFirstName());
        customer.setLastName(req.getLastName());
        customer.setEmail(req.getEmail());
        customer.setPhone(req.getPhone());
        return toResponse(customerRepo.save(customer));
    }

    @Transactional
    public CustomerDTO.Response update(Integer id, CustomerDTO.Request req) {
        Customer customer = customerRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
        if (!customer.getEmail().equals(req.getEmail()) && customerRepo.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + req.getEmail());
        }
        customer.setFirstName(req.getFirstName());
        customer.setLastName(req.getLastName());
        customer.setEmail(req.getEmail());
        customer.setPhone(req.getPhone());
        return toResponse(customerRepo.save(customer));
    }

    @Transactional
    public void delete(Integer id) {
        if (!customerRepo.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found: " + id);
        }
        List<Order> orders = orderRepo.findByCustomerId(id);
        for (Order order : orders) {
            if (order.getShippingAddress() != null) {
                order.setShippingAddress(null);
                orderRepo.save(order);
            }
        }
        customerRepo.deleteById(id);
    }

    public List<AddressDTO.Response> findAddresses(Integer customerId) {
        if (!customerRepo.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found: " + customerId);
        }
        return addressRepo.findByCustomerId(customerId).stream()
                .map(this::toAddressResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AddressDTO.Response addAddress(Integer customerId, AddressDTO.Request req) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + customerId));
        CustomerAddress addr = new CustomerAddress();
        addr.setCustomer(customer);
        addr.setStreetAddress(req.getStreetAddress());
        addr.setPostalCode(req.getPostalCode());
        addr.setCity(req.getCity());
        addr.setCountry(req.getCountry());
        return toAddressResponse(addressRepo.save(addr));
    }

    @Transactional
    public AddressDTO.Response updateAddress(Integer customerId, Integer addressId, AddressDTO.Request req) {
        CustomerAddress addr = addressRepo.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found: " + addressId));
        if (!addr.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("Address does not belong to customer " + customerId);
        }
        addr.setStreetAddress(req.getStreetAddress());
        addr.setPostalCode(req.getPostalCode());
        addr.setCity(req.getCity());
        addr.setCountry(req.getCountry());
        return toAddressResponse(addressRepo.save(addr));
    }

    @Transactional
    public void deleteAddress(Integer customerId, Integer addressId) {
        CustomerAddress addr = addressRepo.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found: " + addressId));
        if (!addr.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("Address does not belong to customer " + customerId);
        }
        List<Order> orders = orderRepo.findByCustomerId(customerId);
        for (Order order : orders) {
            if (order.getShippingAddress() != null && order.getShippingAddress().getId().equals(addressId)) {
                order.setShippingAddress(null);
                orderRepo.save(order);
            }
        }
        addressRepo.delete(addr);
    }

    private CustomerDTO.Response toResponse(Customer customer) {
        CustomerDTO.Response r = new CustomerDTO.Response();
        r.setId(customer.getId());
        r.setFirstName(customer.getFirstName());
        r.setLastName(customer.getLastName());
        r.setEmail(customer.getEmail());
        r.setPhone(customer.getPhone());
        return r;
    }

    private CustomerDTO.DetailResponse toDetailResponse(Customer customer) {
        CustomerDTO.DetailResponse r = new CustomerDTO.DetailResponse();
        r.setId(customer.getId());
        r.setFirstName(customer.getFirstName());
        r.setLastName(customer.getLastName());
        r.setEmail(customer.getEmail());
        r.setPhone(customer.getPhone());
        r.setAddresses(addressRepo.findByCustomerId(customer.getId()).stream()
                .map(this::toAddressResponse)
                .collect(Collectors.toList()));
        return r;
    }

    public AddressDTO.Response toAddressResponse(CustomerAddress addr) {
        AddressDTO.Response r = new AddressDTO.Response();
        r.setId(addr.getId());
        r.setStreetAddress(addr.getStreetAddress());
        r.setPostalCode(addr.getPostalCode());
        r.setCity(addr.getCity());
        r.setCountry(addr.getCountry());
        return r;
    }
}
