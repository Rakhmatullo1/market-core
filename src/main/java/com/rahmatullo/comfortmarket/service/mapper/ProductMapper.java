package com.rahmatullo.comfortmarket.service.mapper;

import com.rahmatullo.comfortmarket.entity.Category;
import com.rahmatullo.comfortmarket.entity.Premise;
import com.rahmatullo.comfortmarket.entity.Product;
import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.repository.CategoryRepository;
import com.rahmatullo.comfortmarket.repository.PremiseRepository;
import com.rahmatullo.comfortmarket.service.AuthService;
import com.rahmatullo.comfortmarket.service.dto.ProductCountDto;
import com.rahmatullo.comfortmarket.service.dto.ProductDto;
import com.rahmatullo.comfortmarket.service.dto.ProductRequestDto;
import com.rahmatullo.comfortmarket.service.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR, componentModel = "spring")
@Slf4j
public abstract  class ProductMapper {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PremiseRepository premiseRepository;
    @Autowired
    private AuthService authService;

    @Mapping(target = "extra", source = "count", qualifiedByName = "getExtra")
    @Mapping(target = "category", expression = "java(product.getCategory().getName())")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "date")

    public abstract ProductDto toProductDto(Product product);

    @Mapping(target = "url", ignore = true)
    @Mapping(target = "createdAt", expression = "java(getCreatedTime())")
    @Mapping(target = "premise", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "category", source = "productRequestDto.categoryId", qualifiedByName = "getCategory")
    @Mapping(target = "addedBy", expression = "java(addedBy(user))")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "productRequestDto.name")
    @Mapping(target = "count", expression = "java(getCount(productRequestDto.getCount(), premise))")
    public abstract Product toProduct(ProductRequestDto productRequestDto, Premise premise, User user);

    @Mapping(target = "id", source = "product.id")
    @Mapping(target = "name", source = "productRequestDto.name")
    @Mapping(target = "barcode", source = "productRequestDto.barcode")
    @Mapping(target = "createdAt", source = "product.createdAt")
    @Mapping(target = "count", expression = "java(changeCount(product.getCount(), productRequestDto.getCount(), premiseId))")
    @Mapping(target = "price", source = "productRequestDto.price")
    @Mapping(target = "owner", source = "product.owner")
    @Mapping(target = "addedBy", source = "product.addedBy")
    @Mapping(target = "premise", source = "product.premise")
    @Mapping(target = "category", source = "product.category")
    public abstract Product toProduct(ProductRequestDto productRequestDto, Product product, Long premiseId);

    @Named("addedBy")
    String addedBy(User user) {
        return user.getUsername();
    }

    @Named("getCategory")
    Category getCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(()->{
            log.warn("Category is not found ");
            throw new NotFoundException("Category is not found "+id);
        });
    }

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

    @Named("getCount")
    List<String> getCount( int count,Premise premise) {
        return List.of(getFormattedString(premise, count));
    }

    @Named("changeCount")
    List<String> changeCount(List<String> initialCount, int count, Long premiseId){
        List<String> productCount = new ArrayList<>();
        if(!Objects.isNull(initialCount)) {
            productCount = initialCount;
        }

        Premise premise = findPremise(premiseId);

        String foundCount = productCount.stream().filter(c->Objects.equals(Long.parseLong(c.split(":")[0]), premiseId)).findFirst().orElseThrow(()->new NotFoundException("Premise is not found"));
        productCount.remove(foundCount);

        productCount.add(getFormattedString(premise,count));
        return productCount;
    }

    private Premise findPremise(Long id) {
        return premiseRepository
                .findByOwnerAndId(authService.getOwner(), id)
                .orElseThrow(()->new NotFoundException("Premise is not found"));
    }

    static public String getFormattedString(Premise premise, Object count) {
        return String.format("%s:%s", premise.getId(), count);
    }
}
