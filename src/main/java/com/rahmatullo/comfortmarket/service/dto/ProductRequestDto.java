package com.rahmatullo.comfortmarket.service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductRequestDto {
    @NotNull(message = "name cannot be null")
    private String name;
    @NotNull(message = "name cannot be null")
    private String barcode;
    @NotNull(message = "name cannot be null")
    private int count;
    @NotNull(message = "name cannot be null")
    private Double price;
    @NotNull(message = "name cannot be null")
    private  Long categoryId;
}
