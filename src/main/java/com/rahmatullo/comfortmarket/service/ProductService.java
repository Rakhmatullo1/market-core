package com.rahmatullo.comfortmarket.service;

import com.rahmatullo.comfortmarket.entity.Product;
import com.rahmatullo.comfortmarket.service.dto.MessageDto;
import com.rahmatullo.comfortmarket.service.dto.ProductDto;
import com.rahmatullo.comfortmarket.service.dto.ProductRequestDto;

import java.util.List;

public interface ProductService {
    List<ProductDto> getProductsByCategoryId(Long categoryId);

    List<ProductDto> getProductsByPremiseId(Long premiseId);

    List<ProductDto> getProductByOwner();

    ProductDto getById(Long id);

    ProductDto updateProduct(Long id, ProductRequestDto productRequestDto);

    MessageDto deleteProduct(Long id);

    Product toProduct(Long id);

    void addProduct2Category( Product product);

    void removeProductsFromPremise(Product product);

    void removeProductFromCategory( Product product);
}
