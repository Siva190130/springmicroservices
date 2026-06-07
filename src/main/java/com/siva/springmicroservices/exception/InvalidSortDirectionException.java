package com.siva.springmicroservices.exception;

public class InvalidSortDirectionException extends RuntimeException {
  public InvalidSortDirectionException(String message) {
    super(message);
  }
}
