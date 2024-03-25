package com.rahmatullo.comfortmarket.service.dto;

import lombok.Data;

@Data
public class ProductDetailsDto {
    private String name;
    private Long count;
    private Double initialPrice;
    private Double finalPrice;
    private String barcode;
}
