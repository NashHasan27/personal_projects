package org.example.exception;

// Define a custom exception for better clarity
public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(String message) {
        super(message);
    }
}
