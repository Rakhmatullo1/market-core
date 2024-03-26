package com.rahmatullo.comfortmarket.service.impl;

import com.rahmatullo.comfortmarket.entity.ProductInfo;
import com.rahmatullo.comfortmarket.repository.ProductInfoRepository;
import com.rahmatullo.comfortmarket.service.ProductInfoService;
import com.rahmatullo.comfortmarket.service.dto.ProductInfoDto;
import com.rahmatullo.comfortmarket.service.dto.request.ProductInfoRequestDto;
import com.rahmatullo.comfortmarket.service.exception.ExistsException;
import com.rahmatullo.comfortmarket.service.exception.NotFoundException;
import com.rahmatullo.comfortmarket.service.mapper.ProductInfoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductInfoServiceImpl implements ProductInfoService {

    private final ProductInfoRepository productInfoRepository;
    private final ProductInfoMapper productInfoMapper;

    @Override
    public ProductInfoDto create(ProductInfoRequestDto productInfoRequestDto) {
        log.info("Requested to create product info");

        if(productInfoRepository.existsByBarcode(productInfoRequestDto.getBarcode())){
            log.warn("Barcode exists");
            throw new ExistsException("Product exists in database");
        }

        ProductInfo productInfo = productInfoMapper.toProductInfo(productInfoRequestDto);
        return productInfoMapper.toProductInfoDto(productInfoRepository.save(productInfo));
    }

    @Override
    public List<ProductInfoDto> getAll(Pageable pageable) {
        log.info("Requested to get all product info");
        return productInfoRepository
                .findAll(pageable)
                .stream()
                .map(productInfoMapper::toProductInfoDto)
                .toList();
    }

    @Override
    public ProductInfoDto getById(Long id) {
        log.info("Requested to get product info");
        ProductInfo productInfo = productInfoRepository.findById(id)
                .orElseThrow(()->new NotFoundException("Product is not found"));
        return productInfoMapper.toProductInfoDto(productInfo);
    }
}
