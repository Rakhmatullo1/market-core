package com.rahmatullo.comfortmarket.service.mapper;

import com.rahmatullo.comfortmarket.entity.*;
import com.rahmatullo.comfortmarket.repository.CategoryRepository;
import com.rahmatullo.comfortmarket.service.dto.ProductInfoDto;
import com.rahmatullo.comfortmarket.service.dto.request.ProductInfoRequestDto;
import com.rahmatullo.comfortmarket.service.exception.NotFoundException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class  ProductInfoMapper {

    @Autowired
    private  CategoryRepository categoryRepository;

    @Mapping(target = "category", source = "categoryId", qualifiedByName = "getCategory")
    @Mapping(target = "id", ignore = true)
    public abstract ProductInfo toProductInfo(ProductInfoRequestDto productInfoRequestDto);

    @Mapping(target = "category", expression = "java(productInfo.getCategory().getName())")
    public abstract ProductInfoDto toProductInfoDto(ProductInfo productInfo);

    @Named("getCategory")
    Category getCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(()->new NotFoundException("category is not found" + id));
    }
}
