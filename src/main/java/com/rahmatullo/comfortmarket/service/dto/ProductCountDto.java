package com.rahmatullo.comfortmarket.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductCountDto {
    private String premise;
    private int count;
}
