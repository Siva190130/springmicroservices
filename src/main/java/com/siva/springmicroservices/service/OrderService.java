package com.siva.springmicroservices.service;

import com.siva.springmicroservices.dto.OrderRequest;
import com.siva.springmicroservices.dto.OrderResponse;
import com.siva.springmicroservices.dto.OrderStatusUpdateRequest;
import com.siva.springmicroservices.dto.PagedResponse;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(OrderRequest request);

    PagedResponse<OrderResponse> getAllOrders(
            int page,
            int size,
            String sortBy,
            String direction);

    PagedResponse<OrderResponse> getMyOrders(
            int page,
            int size,
            String sortBy,
            String direction);

    OrderResponse getOrderById(Long orderId);

    OrderResponse updateOrderStatus(
            Long orderId,
            OrderStatusUpdateRequest request);

    OrderResponse cancelOrder(Long orderId);

}