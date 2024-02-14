package com.rahmatullo.comfortmarket.service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorDto {
    private String message;
    private Long timeStamp;
    private int status;
}
