package com.rahmatullo.comfortmarket.service.dto;

import lombok.Data;


@Data
public class ProductDto {
    private Long id;
    private String name;
    private String barcode;
    private String createdAt;
    private int count;
    private Double price;
    private  String category;
}
