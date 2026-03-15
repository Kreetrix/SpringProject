package com.webstore.controller;

import com.webstore.dto.CustomerStatsDTO;
import com.webstore.dto.OrderDTO;
import com.webstore.model.OrderStatusHistory;
import com.webstore.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<OrderDTO.Response> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        if (status != null) return orderService.findByStatus(status);
        if (customerId != null) return orderService.findByCustomer(customerId);
        if (from != null && to != null) return orderService.findByDateRange(from, to);
        return orderService.findAll();
    }

    @GetMapping("/{id}")
    public OrderDTO.Response getById(@PathVariable Integer id) {
        return orderService.findById(id);
    }

    @PostMapping
    public ResponseEntity<OrderDTO.Response> create(@Valid @RequestBody OrderDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(req));
    }

    @PatchMapping("/{id}/status")
    public OrderDTO.Response updateStatus(@PathVariable Integer id,
                                          @Valid @RequestBody OrderDTO.StatusUpdateRequest req) {
        return orderService.updateStatus(id, req);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Integer id) {
        orderService.cancel(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/history")
    public List<OrderStatusHistory> getStatusHistory(@PathVariable Integer id) {
        return orderService.getStatusHistory(id);
    }

    @GetMapping("/customer-stats")
    public List<CustomerStatsDTO> getCustomerStats() {
        return orderService.getCustomerStats();
    }
}
