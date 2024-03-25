package com.rahmatullo.comfortmarket.service.dto;

import lombok.Data;

import java.util.List;

@Data
public class InvoiceDto {
    private Long id;
    private String description;
    private String date;
    private String premise;
    private Double overallPrice;
    private List<ProductDetailsDto> products;
}
