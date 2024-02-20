package com.rahmatullo.comfortmarket.service;

import com.rahmatullo.comfortmarket.service.dto.MessageDto;
import com.rahmatullo.comfortmarket.service.dto.ProductDto;
import com.rahmatullo.comfortmarket.service.dto.ProductRequestDto;

import java.util.List;

public interface ProductService {
    List<ProductDto> getProductsByCategoryId(Long categoryId);

    List<ProductDto> getProductsByPremiseId(Long premiseId);

    List<ProductDto> getProductByOwner();

    ProductDto updateProduct(Long id, ProductRequestDto productRequestDto);

    MessageDto deleteProduct(Long id);
}
