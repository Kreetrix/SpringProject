package com.webstore.service;

import com.webstore.dto.CustomerStatsDTO;
import com.webstore.dto.OrderDTO;
import com.webstore.exception.ResourceNotFoundException;
import com.webstore.model.Customer;
import com.webstore.model.CustomerAddress;
import com.webstore.model.Order;
import com.webstore.model.OrderItem;
import com.webstore.model.OrderStatusHistory;
import com.webstore.model.Product;
import com.webstore.repository.CustomerAddressRepository;
import com.webstore.repository.CustomerRepository;
import com.webstore.repository.OrderRepository;
import com.webstore.repository.OrderStatusHistoryRepository;
import com.webstore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepo;
    private final CustomerRepository customerRepo;
    private final ProductRepository productRepo;
    private final CustomerAddressRepository addressRepo;
    private final OrderStatusHistoryRepository historyRepo;

    @Transactional(readOnly = true)
    public List<OrderDTO.Response> findAll() {
        return orderRepo.findAllWithDetails().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderDTO.Response findById(Integer id) {
        Order order = orderRepo.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderDTO.Response> findByCustomer(Integer customerId) {
        if (!customerRepo.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found: " + customerId);
        }
        return orderRepo.findByCustomerId(customerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderDTO.Response> findByStatus(String status) {
        return orderRepo.findByStatus(status.toUpperCase()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderDTO.Response> findByDateRange(LocalDateTime from, LocalDateTime to) {
        return orderRepo.findByDateRange(from, to).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderDTO.Response create(OrderDTO.Request req) {
        Customer customer = customerRepo.findById(req.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + req.getCustomerId()));

        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus("NEW");
        order.setOrderDate(LocalDateTime.now());

        if (req.getShippingAddressId() != null) {
            CustomerAddress addr = addressRepo.findById(req.getShippingAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException("Address not found: " + req.getShippingAddressId()));
            if (!addr.getCustomer().getId().equals(customer.getId())) {
                throw new IllegalArgumentException("Shipping address does not belong to this customer");
            }
            order.setShippingAddress(addr);
        }

        Order savedOrder = orderRepo.save(order);

        for (OrderDTO.OrderItemRequest itemReq : req.getItems()) {
            Product product = productRepo.findByIdWithDetails(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + itemReq.getProductId()));

            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setProduct(product);
            item.setQuantity(itemReq.getQuantity());
            item.setUnitPrice(product.getPrice());
            savedOrder.getItems().add(item);
        }

        orderRepo.save(savedOrder);
        return toResponse(orderRepo.findByIdWithDetails(savedOrder.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found after save")));
    }

    @Transactional
    public OrderDTO.Response updateStatus(Integer id, OrderDTO.StatusUpdateRequest req) {
        Order order = orderRepo.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));

        String newStatus = req.getStatus().toUpperCase();
        validateStatusTransition(order.getStatus(), newStatus);
        order.setStatus(newStatus);

        if (newStatus.equals("DELIVERED")) {
            order.setDeliveryDate(LocalDateTime.now());
        }

        orderRepo.save(order);
        return toResponse(orderRepo.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id)));
    }

    @Transactional
    public void cancel(Integer id) {
        Order order = orderRepo.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
        if (order.getStatus().equals("DELIVERED")) {
            throw new IllegalArgumentException("Cannot cancel a delivered order");
        }
        order.setStatus("CANCELLED");
        orderRepo.save(order);
    }

    @Transactional(readOnly = true)
    public List<OrderStatusHistory> getStatusHistory(Integer orderId) {
        if (!orderRepo.existsById(orderId)) {
            throw new ResourceNotFoundException("Order not found: " + orderId);
        }
        return historyRepo.findByOrderIdOrderByChangedAtAsc(orderId);
    }

    @Transactional(readOnly = true)
    public List<CustomerStatsDTO> getCustomerStats() {
        List<Customer> customers = customerRepo.findAll();
        List<CustomerStatsDTO> result = new ArrayList<>();

        for (Customer customer : customers) {
            List<Order> customerOrders = orderRepo.findByCustomerId(customer.getId());

            BigDecimal totalSpent = BigDecimal.ZERO;
            for (Order order : customerOrders) {
                for (OrderItem item : order.getItems()) {
                    totalSpent = totalSpent.add(
                            item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                }
            }

            LocalDateTime lastOrderDate = null;
            for (Order order : customerOrders) {
                if (lastOrderDate == null || order.getOrderDate().isAfter(lastOrderDate)) {
                    lastOrderDate = order.getOrderDate();
                }
            }

            CustomerStatsDTO dto = new CustomerStatsDTO();
            dto.setCustomerId(customer.getId());
            dto.setCustomerName(customer.getFirstName() + " " + customer.getLastName());
            dto.setEmail(customer.getEmail());
            dto.setTotalOrders((long) customerOrders.size());
            dto.setTotalSpent(totalSpent);
            dto.setLastOrderDate(lastOrderDate);
            result.add(dto);
        }

        return result;
    }

    private void validateStatusTransition(String current, String next) {
        List<String> allowed;
        switch (current) {
            case "NEW":
                allowed = List.of("PROCESSING", "CANCELLED");
                break;
            case "PROCESSING":
                allowed = List.of("SHIPPED", "CANCELLED");
                break;
            case "SHIPPED":
                allowed = List.of("DELIVERED");
                break;
            case "DELIVERED":
            case "CANCELLED":
                allowed = List.of();
                break;
            default:
                allowed = List.of("PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED");
        }
        if (!allowed.contains(next)) {
            throw new IllegalArgumentException(
                    "Invalid status transition from " + current + " to " + next + ". Allowed: " + allowed);
        }
    }

    public OrderDTO.Response toResponse(Order order) {
        OrderDTO.Response response = new OrderDTO.Response();
        response.setId(order.getId());
        response.setCustomerId(order.getCustomer().getId());
        response.setCustomerName(order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName());
        response.setOrderDate(order.getOrderDate());
        response.setDeliveryDate(order.getDeliveryDate());
        response.setStatus(order.getStatus());

        List<OrderDTO.OrderItemResponse> itemResponses = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            OrderDTO.OrderItemResponse ir = new OrderDTO.OrderItemResponse();
            ir.setProductId(item.getProduct().getId());
            ir.setProductName(item.getProduct().getName());
            ir.setQuantity(item.getQuantity());
            ir.setUnitPrice(item.getUnitPrice());
            ir.setSubtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            itemResponses.add(ir);
        }

        BigDecimal total = BigDecimal.ZERO;
        for (OrderDTO.OrderItemResponse ir : itemResponses) {
            total = total.add(ir.getSubtotal());
        }

        response.setItems(itemResponses);
        response.setTotalAmount(total);
        return response;
    }
}
