package com.sparkminds.ecommerce.exception;

public class ConflictResourceException extends RuntimeException {
    public ConflictResourceException(String message) {
        super(message);
    }
}
