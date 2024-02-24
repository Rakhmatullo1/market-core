package com.rahmatullo.comfortmarket.service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ErrorResponseValidation {
    private List<String> errors;
    private long timeStamp;
    private int httpStatusCode;
}
