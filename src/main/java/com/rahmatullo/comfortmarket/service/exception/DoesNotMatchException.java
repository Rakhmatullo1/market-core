package com.rahmatullo.comfortmarket.service.exception;

public class DoesNotMatchException extends RuntimeException{
    public DoesNotMatchException(String message) {
        super(message);
    }
}
