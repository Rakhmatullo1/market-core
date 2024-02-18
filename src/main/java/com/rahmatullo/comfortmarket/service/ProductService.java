package com.rahmatullo.comfortmarket.service;

import com.rahmatullo.comfortmarket.service.dto.ProductDto;

import java.util.List;

public interface ProductService {
    List<ProductDto> getProductsByCategoryId(Long categoryId);

    List<ProductDto> getProductsByPremiseId(Long premiseId);

    List<ProductDto> getProductByOwner();
}
