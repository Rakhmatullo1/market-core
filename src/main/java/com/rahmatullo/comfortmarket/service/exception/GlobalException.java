package com.rahmatullo.comfortmarket.service.exception;

import com.rahmatullo.comfortmarket.service.dto.ErrorDto;
import com.rahmatullo.comfortmarket.service.dto.ErrorResponseValidation;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
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

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorDto> usernameNotFoundExceptionHandler(UsernameNotFoundException ex) {
        return handler(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDto> fileUploadExceptionHandler(FileUploadException ex) {
        return handler(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDto> httpRequestMethodNotSupportedExceptionHandle(HttpRequestMethodNotSupportedException   ex) {
        return handler(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDto> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException ex) {
        return handler(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler
    public  ResponseEntity<ErrorDto> illegalStateExceptionHandler(IllegalStateException ex){
        return  handler(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDto> malformedJwtExceptionHandler(MalformedJwtException ex) {
        return handler(HttpStatus.BAD_REQUEST, ex.getMessage());
    }


    @ExceptionHandler
    public ResponseEntity<ErrorResponseValidation> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex) {
        log.warn(ex.getMessage());
        ErrorResponseValidation errors = ErrorResponseValidation
                .builder()
                .errors(ex.getBindingResult().getAllErrors().stream().map(error-> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    return String.format("%s: %s", fieldName, errorMessage);
                }).toList())
                .httpStatusCode(HttpStatus.BAD_REQUEST.value())
                .timeStamp(System.currentTimeMillis())
                .build();
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ErrorDto> handler(HttpStatus status, String message) {
        log.info(message);
        return new ResponseEntity<>(ErrorDto
                .builder()
                .message(message)
                .timeStamp(System.currentTimeMillis())
                .status(status.value())
                .build(), status);
    }
}
