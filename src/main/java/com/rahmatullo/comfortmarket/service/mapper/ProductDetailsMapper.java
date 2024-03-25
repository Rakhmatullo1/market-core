package com.rahmatullo.comfortmarket.service.mapper;

import com.rahmatullo.comfortmarket.entity.ProductDetails;
import com.rahmatullo.comfortmarket.entity.ProductInfo;
import com.rahmatullo.comfortmarket.repository.ProductInfoRepository;
import com.rahmatullo.comfortmarket.service.dto.ProductDetailsDto;
import com.rahmatullo.comfortmarket.service.dto.request.ProductDetailsRequestDto;
import com.rahmatullo.comfortmarket.service.exception.NotFoundException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ProductDetailsMapper {

    @Autowired
    private ProductInfoRepository productInfoRepository;

    @Mapping(target = "productInfo", source = "productId", qualifiedByName = "getProductInfo")
    @Mapping(target = "id", ignore = true)
    public abstract ProductDetails toProductDetails(ProductDetailsRequestDto requestDto);

    @Mapping(target = "barcode", expression = "java(productDetails.getProductInfo().getBarcode())")
    @Mapping(target = "name", expression = "java(productDetails.getProductInfo().getName())")
    public abstract ProductDetailsDto toProductDetailsDto(ProductDetails productDetails);

    @Named("getProductInfo")
    ProductInfo getProductInfo(Long id) {
        return productInfoRepository.findById(id).orElseThrow(()->new NotFoundException("Product is not found"));
    }
}
