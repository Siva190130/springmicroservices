package com.siva.springmicroservices.exception;

public class OrderCancellationNotAllowedException
        extends RuntimeException {

    public OrderCancellationNotAllowedException(Long orderId) {

        super("Order with id "
                + orderId
                + " cannot be cancelled in its current status");
    }
}