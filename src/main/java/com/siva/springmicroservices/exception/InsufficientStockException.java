package com.siva.springmicroservices.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String productName) {
        super("Insufficient stock available for product: " + productName);
    }
}