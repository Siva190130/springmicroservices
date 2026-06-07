package com.siva.springmicroservices.repo;

import com.siva.springmicroservices.entity.Order;
import com.siva.springmicroservices.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUser(
            User user,
            Pageable pageable);

}