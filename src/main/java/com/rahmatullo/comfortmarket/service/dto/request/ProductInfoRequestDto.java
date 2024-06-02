package com.rahmatullo.comfortmarket.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductInfoRequestDto {
    private String name;
    private String barcode;
    private String producer;
    private Long categoryId;
    @NotBlank(message = "Article should be not null")
    private String article;
}
