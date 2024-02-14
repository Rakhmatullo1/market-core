package com.rahmatullo.comfortmarket.service.exception;

import com.rahmatullo.comfortmarket.service.dto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalException {

    @ExceptionHandler
    public ResponseEntity<ErrorDto> usernameNotFoundHandler(NotFoundException ex) {
        return handler(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDto> emptyExceptionHandler(EmptyFieldException ex) {
        return handler(HttpStatus.UNAUTHORIZED,ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDto> existsExceptionHandler(ExistsException ex) {
        return handler(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDto> existsExceptionHandler(DoesNotMatchException ex) {
        return handler(HttpStatus.CONFLICT, ex.getMessage());
    }

    private ResponseEntity<ErrorDto> handler(HttpStatus status, String message) {
        return new ResponseEntity<>(ErrorDto
                .builder()
                .message(message)
                .timeStamp(System.currentTimeMillis())
                .status(status.value())
                .build(), status);
    }
}
