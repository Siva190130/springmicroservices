package com.siva.springmicroservices.exception;


public class ProductNotFoundException
        extends RuntimeException {

    public ProductNotFoundException(String message) {
        super(message);
    }
}