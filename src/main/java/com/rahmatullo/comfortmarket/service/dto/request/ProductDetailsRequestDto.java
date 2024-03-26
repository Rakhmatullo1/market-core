package com.rahmatullo.comfortmarket.service.dto.request;

import lombok.Data;

@Data
public class ProductDetailsRequestDto {
    private Long productId;
    private Long count;
    private Double initialPrice;
    private Double finalPrice;
}
