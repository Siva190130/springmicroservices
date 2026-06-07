package com.siva.springmicroservices.repo;

import com.siva.springmicroservices.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository
        extends JpaRepository<OrderItem, Long> {
}