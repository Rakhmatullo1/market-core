package com.rahmatullo.comfortmarket.service.dto;

import lombok.Data;

@Data
public class ProductInfoRequestDto {
    private String name;
    private String barcode;
    private String producer;
    private Long categoryId;
}
