package com.rahmatullo.comfortmarket.service.dto;

import lombok.Data;

@Data
public class CategoryDto {
    private Long id;
    private String name;
    private int productsCount;
}
