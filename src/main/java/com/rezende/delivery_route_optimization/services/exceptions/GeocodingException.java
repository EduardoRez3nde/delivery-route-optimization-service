package com.rezende.delivery_route_optimization.services.exceptions;

public class GeocodingException extends RuntimeException {

    public GeocodingException(final String message) { super(message); }
    public GeocodingException(final String message, Throwable cause) { super(message, cause); }

}
