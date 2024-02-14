package com.rahmatullo.comfortmarket.service.exception;

public class ExistsException extends RuntimeException{
    public ExistsException(String message) {
        super(message);
    }
}
