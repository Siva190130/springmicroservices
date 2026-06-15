package com.siva.springmicroservices.exception;

public class InvalidOrderStatusTransitionException
        extends RuntimeException {

    public InvalidOrderStatusTransitionException(
            String currentStatus,
            String newStatus) {

        super("Cannot change order status from "
                + currentStatus
                + " to "
                + newStatus);
    }
}