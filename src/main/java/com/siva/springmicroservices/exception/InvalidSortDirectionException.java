package com.siva.springmicroservices.exception;

public class InvalidSortDirectionException extends RuntimeException {

    public InvalidSortDirectionException(String direction) {
        super("Invalid sort direction: " + direction +
                ". Allowed values: asc, desc");
    }
}