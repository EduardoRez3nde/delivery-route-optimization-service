package com.rezende.delivery_route_optimization.services.exceptions;

public class TooManyRequestsException extends RuntimeException {
    public TooManyRequestsException(String message) { super(message); }
}
