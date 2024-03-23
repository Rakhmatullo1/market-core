package com.rahmatullo.comfortmarket.service;

import com.rahmatullo.comfortmarket.service.dto.ProductInfoDto;
import com.rahmatullo.comfortmarket.service.dto.ProductInfoRequestDto;

public interface ProductInfoService {
    ProductInfoDto create(ProductInfoRequestDto productInfoRequestDto);
}
