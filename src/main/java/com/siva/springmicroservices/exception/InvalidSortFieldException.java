package com.siva.springmicroservices.exception;

public class InvalidSortFieldException extends RuntimeException {
  public InvalidSortFieldException(String message) {
    super(message);
  }
}
