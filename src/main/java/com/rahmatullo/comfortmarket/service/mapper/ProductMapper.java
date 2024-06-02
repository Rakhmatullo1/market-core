package com.rahmatullo.comfortmarket.service.mapper;

import com.rahmatullo.comfortmarket.entity.Premise;
import com.rahmatullo.comfortmarket.entity.Product;
import com.rahmatullo.comfortmarket.entity.ProductDetails;
import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.service.dto.ProductCountDto;
import com.rahmatullo.comfortmarket.service.dto.ProductDto;
import com.rahmatullo.comfortmarket.service.utils.AuthUtils;
import com.rahmatullo.comfortmarket.service.utils.PremiseUtils;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR, componentModel = "spring")
@Slf4j
public abstract  class ProductMapper {

    @Autowired
    private PremiseUtils premiseUtils;
    @Autowired
    private AuthUtils authUtils;

    @Mapping(target = "extra", source = "count", qualifiedByName = "getExtra")
    @Mapping(target = "category", expression = "java(product.getCategory().getName())")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "date")
    public abstract ProductDto toProductDto(Product product);

    @Mapping(target = "article", expression = "java(productDetails.getProductInfo().getArticle())")
    @Mapping(target = "premise", ignore = true)
    @Mapping(target = "url", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "owner", expression = "java(getOwner())")
    @Mapping(target = "name", expression = "java(productDetails.getProductInfo().getName())")
    @Mapping(target = "createdAt", expression = "java(getCreatedTime())")
    @Mapping(target = "category", expression = "java(productDetails.getProductInfo().getCategory())")
    @Mapping(target = "barcode", expression = "java(productDetails.getProductInfo().getBarcode())")
    @Mapping(target = "addedBy", expression = "java(getCreator())")
    @Mapping(target = "count", ignore = true)
    @Mapping(target = "id", ignore = true)
    public abstract Product toProduct(ProductDetails productDetails);

    Date getCreatedTime() {
        return new Date(System.currentTimeMillis());
    }

    @Named("date")
    String date(Date date) {
        return date.toString();
    }

    @Named("getExtra")
    List<ProductCountDto> getExtra(List<String> count) {
        return count.stream().map(c->{
            String[] parts = c.split(":");

            Premise premise = findPremise(Long.parseLong(parts[0]));

            return new ProductCountDto(premise.getName(), Integer.parseInt(parts[1]));
        }).toList();
    }

    private Premise findPremise(Long id) {
        return premiseUtils.getPremise(id);
    }

    static public String getFormattedString(Premise premise, Object count) {
        return String.format("%s:%s", premise.getId(), count);
    }

    User getOwner() {
        return authUtils.getOwner();
    }

    String getCreator() {
        return authUtils.getUser().getUsername();
    }
}
