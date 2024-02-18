package com.rahmatullo.comfortmarket.service.mapper;

import com.rahmatullo.comfortmarket.entity.Premise;
import com.rahmatullo.comfortmarket.entity.Product;
import com.rahmatullo.comfortmarket.service.dto.PremiseDto;
import com.rahmatullo.comfortmarket.service.dto.PremiseRequestDto;
import com.rahmatullo.comfortmarket.service.enums.PremiseType;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR ,componentModel = "spring")
@Slf4j
public abstract class PremiseMapper {

    @Autowired
    private ProductMapper productMapper;

    @Mapping(target = "owner", source = "premise.owner.fullName")
    @Mapping(target = "productsCount", source = "premise.products", qualifiedByName = "getProducts")
    @Mapping(target = "premiseType", expression = "java(premise.getType().name())")
    public abstract PremiseDto toPremiseDto(Premise premise);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "type", source = "premiseRequestDto.premiseType",qualifiedByName = "getType")
    @Mapping(target = "id", ignore = true)
     public abstract Premise toPremise(PremiseRequestDto premiseRequestDto);

    @Named("getProducts")
    int getProducts(List<Product> productList) {
        return productList.size();
    }

    @Named("getType")
    PremiseType getType(String premiseType){
        return Arrays.stream(PremiseType.values())
                .filter(type-> Objects.equals(premiseType, type.name()))
                .findFirst()
                .orElseThrow(()->{
            log.warn("Premise type is not found");
            throw new RuntimeException("Premise type is not found "+premiseType);
        });
    }
}
