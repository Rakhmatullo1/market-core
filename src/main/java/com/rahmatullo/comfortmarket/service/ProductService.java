package com.rahmatullo.comfortmarket.service;

import com.rahmatullo.comfortmarket.entity.Product;
import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.service.dto.MessageDto;
import com.rahmatullo.comfortmarket.service.dto.ProductDto;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ProductService {
    List<ProductDto> getProductsByCategoryId(Long categoryId,  PageRequest pageRequest);

    List<ProductDto> getProductsByPremiseId(Long premiseId, PageRequest pageRequest);

    List<ProductDto> getProductByOwner(PageRequest pageRequest);

    ProductDto getById(Long id);

    MessageDto deleteProduct(Long id);

    Product toProduct(Long id, User owner);
}
