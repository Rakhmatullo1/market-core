package com.rahmatullo.comfortmarket.service.mapper;

import com.rahmatullo.comfortmarket.entity.Category;
import com.rahmatullo.comfortmarket.entity.Product;
import com.rahmatullo.comfortmarket.service.dto.CategoryDto;
import com.rahmatullo.comfortmarket.service.dto.request.CategoryRequestDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Objects;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR, componentModel = "spring")
public abstract class CategoryMapper {

    @Mapping(target = "productList", ignore = true)
    @Mapping(target = "id", ignore = true)
    public abstract Category toCategory(CategoryRequestDto categoryRequestDto);

    @Mapping(target = "productsCount", source = "category.productList", qualifiedByName = "getProducts")
    public abstract CategoryDto toCategoryDto(Category category);

    @Named("getProducts")
    int getProducts(List<Product> products) {
        if(Objects.isNull(products)){
            return 0;
        }
        return products.size();
    }
}
