package com.rahmatullo.comfortmarket.service.mapper;

import com.rahmatullo.comfortmarket.entity.History;
import com.rahmatullo.comfortmarket.entity.Product;
import com.rahmatullo.comfortmarket.service.dto.HistoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Date;

@Mapper(componentModel = "spring")
public interface HistoryMapper {

    @Mapping(target = "productName", source = "product", qualifiedByName = "getProductName")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "getDate")
    HistoryDto toHistoryDto(History history);

    @Named("getProductName")
    default String getProductName(Product product) {
        return product.getName();
    }

    @Named("getDate")
    default String getDate(Date date) {
        return date.toString();
    }
}
