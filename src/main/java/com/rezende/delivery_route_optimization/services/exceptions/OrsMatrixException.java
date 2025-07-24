package com.rezende.delivery_route_optimization.services.exceptions;

public class OrsMatrixException extends RuntimeException {
    public OrsMatrixException(String message) { super(message); }
    public OrsMatrixException(String message, Throwable cause) { super(message, cause); }
}