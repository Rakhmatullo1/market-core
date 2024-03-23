package com.rahmatullo.comfortmarket.service.impl;

import com.rahmatullo.comfortmarket.service.ProductInfoService;
import com.rahmatullo.comfortmarket.service.dto.ProductInfoDto;
import com.rahmatullo.comfortmarket.service.dto.ProductInfoRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductInfoServiceImpl implements ProductInfoService {
    @Override
    public ProductInfoDto create(ProductInfoRequestDto productInfoRequestDto) {
        return null;
    }
}
