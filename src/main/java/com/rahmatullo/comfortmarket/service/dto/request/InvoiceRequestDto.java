package com.rahmatullo.comfortmarket.service.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class InvoiceRequestDto {
    private String description;
    private Long premiseId;
    private List<ProductDetailsRequestDto> products;
}
