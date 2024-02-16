package com.rahmatullo.comfortmarket.service.dto;

import lombok.Data;

import java.util.List;

@Data
public class PremiseDto {
    private Long id;
    private String premiseType;
    private String address;
    private String name;
    private List<ProductDto> productDto;
    private String owner;
}
