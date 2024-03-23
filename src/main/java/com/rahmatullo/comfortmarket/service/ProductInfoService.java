package com.rahmatullo.comfortmarket.service;

import com.rahmatullo.comfortmarket.service.dto.ProductInfoDto;
import com.rahmatullo.comfortmarket.service.dto.ProductInfoRequestDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductInfoService {
    ProductInfoDto create(ProductInfoRequestDto productInfoRequestDto);

    List<ProductInfoDto> getAll(Pageable pageable);

    ProductInfoDto getById(Long id);
}
