package com.rahmatullo.comfortmarket.service.dto;

import lombok.Data;

@Data
public class ProductInfoDto {
    private Long id;
    private String name;
    private String producer;
    private String barcode;
    private String category;
}
