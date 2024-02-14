package com.rahmatullo.comfortmarket.service.exception;

public class EmptyFieldException extends RuntimeException{
    public EmptyFieldException(String message) {
        super(message);
    }
}
