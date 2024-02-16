package com.rahmatullo.comfortmarket.service.dto;

import lombok.Data;

@Data
public class ProductRequestDto {
    private String name;
    private String barcode;
    private String createdAt;
    private int count;
    private Double price;
    private  Long categoryId;
}
