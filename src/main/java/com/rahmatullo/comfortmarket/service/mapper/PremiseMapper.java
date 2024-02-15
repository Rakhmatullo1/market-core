package com.rahmatullo.comfortmarket.service.mapper;

import com.rahmatullo.comfortmarket.entity.Premise;
import com.rahmatullo.comfortmarket.entity.Product;
import com.rahmatullo.comfortmarket.service.dto.PremiseDto;
import com.rahmatullo.comfortmarket.service.dto.ProductDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR ,componentModel = "spring")
public abstract class PremiseMapper {

    @Autowired
    private ProductMapper productMapper;

    @Mapping(target = "productDto", expression = "java(getProducts(premise.getProducts()))")
    @Mapping(target = "premiseType", expression = "java(premise.getType().name())")
    public abstract PremiseDto toPremiseDto(Premise premise);

    List<ProductDto> getProducts(List<Product> productList) {
        return productList.stream().map(product -> productMapper.toProductDto(product)).toList();
    }
}
