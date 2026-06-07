package com.siva.springmicroservices.service.impl;

import com.siva.springmicroservices.dto.*;
import com.siva.springmicroservices.entity.*;
import com.siva.springmicroservices.exception.InsufficientStockException;
import com.siva.springmicroservices.exception.ProductNotFoundException;
import com.siva.springmicroservices.repo.OrderRepository;
import com.siva.springmicroservices.repo.ProductRepository;
import com.siva.springmicroservices.repo.UserRepository;
import com.siva.springmicroservices.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    private final UserRepository userRepository;

    @Override
    public OrderResponse createOrder(OrderRequest request) {

        User currentUser = getCurrentUser();

        Order order = Order.builder()
                .user(currentUser)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .build();

        List<OrderItem> orderItems = new ArrayList<>();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {

            Product product = productRepository.findById(
                            itemRequest.getProductId())
                    .orElseThrow(() ->
                            new ProductNotFoundException(
                                    itemRequest.getProductId()));

            validateStock(product, itemRequest.getQuantity());

            BigDecimal subtotal =
                    product.getPrice()
                            .multiply(BigDecimal.valueOf(
                                    itemRequest.getQuantity()));

            totalAmount = totalAmount.add(subtotal);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .price(product.getPrice())
                    .build();

            orderItems.add(orderItem);

            product.setQuantity(
                    product.getQuantity()
                            - itemRequest.getQuantity());
        }

        order.setOrderItems(orderItems);

        order.setTotalAmount(totalAmount);

        Order savedOrder =
                orderRepository.save(order);

        return mapToOrderResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> getAllOrders(
            int page,
            int size,
            String sortBy,
            String direction) {

        Pageable pageable =
                createPageable(
                        page,
                        size,
                        sortBy,
                        direction);

        Page<Order> orderPage =
                orderRepository.findAll(pageable);

        List<OrderResponse> orders =
                orderPage.getContent()
                        .stream()
                        .map(this::mapToOrderResponse)
                        .toList();

        return mapToPagedResponse(
                orders,
                orderPage);
    }


    @Override
    @Transactional(readOnly = true)
    public PagedResponse<OrderResponse> getMyOrders(
            int page,
            int size,
            String sortBy,
            String direction) {

        User currentUser = getCurrentUser();

        Pageable pageable =
                createPageable(
                        page,
                        size,
                        sortBy,
                        direction);

        Page<Order> orderPage =
                orderRepository.findByUser(
                        currentUser,
                        pageable);

        List<OrderResponse> orders =
                orderPage.getContent()
                        .stream()
                        .map(this::mapToOrderResponse)
                        .toList();

        return mapToPagedResponse(
                orders,
                orderPage);
    }

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    private void validateStock(
            Product product,
            Integer requestedQuantity) {

        if (product.getQuantity() < requestedQuantity) {
            throw new InsufficientStockException(
                    product.getName());
        }
    }

    private OrderResponse mapToOrderResponse(Order order) {

        return OrderResponse.builder()
                .orderId(order.getId())
                .userId(order.getUser().getId())
                .userName(order.getUser().getName())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .items(
                        order.getOrderItems()
                                .stream()
                                .map(this::mapToOrderItemResponse)
                                .toList()
                )
                .build();
    }

    private OrderItemResponse mapToOrderItemResponse(
            OrderItem orderItem) {

        BigDecimal subtotal =
                orderItem.getPrice()
                        .multiply(
                                BigDecimal.valueOf(
                                        orderItem.getQuantity()));

        return OrderItemResponse.builder()
                .productId(orderItem.getProduct().getId())
                .productName(orderItem.getProduct().getName())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .subtotal(subtotal)
                .build();
    }

    private PagedResponse<OrderResponse> mapToPagedResponse(
            List<OrderResponse> orders,
            Page<Order> orderPage) {

        return PagedResponse.<OrderResponse>builder()
                .content(orders)
                .pageNumber(orderPage.getNumber())
                .pageSize(orderPage.getSize())
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .last(orderPage.isLast())
                .build();
    }

    private Pageable createPageable(
            int page,
            int size,
            String sortBy,
            String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        return PageRequest.of(page, size, sort);
    }
}