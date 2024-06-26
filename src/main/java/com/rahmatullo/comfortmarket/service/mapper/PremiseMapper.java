package com.rahmatullo.comfortmarket.service.mapper;

import com.rahmatullo.comfortmarket.entity.Premise;
import com.rahmatullo.comfortmarket.entity.Product;
import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.service.dto.PremiseDto;
import com.rahmatullo.comfortmarket.service.dto.request.PremiseRequestDto;
import com.rahmatullo.comfortmarket.service.dto.ProductDto;
import com.rahmatullo.comfortmarket.service.dto.UserDto;
import com.rahmatullo.comfortmarket.service.enums.UserRole;
import com.rahmatullo.comfortmarket.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR ,componentModel = "spring")
@Slf4j
public abstract class PremiseMapper {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserMapper userMapper;

    @Mapping(target = "owner", source = "premise.owner.fullName")
    @Mapping(target = "type", expression = "java(premise.getType().name())")
    @Mapping(target = "workers", source = "workers", qualifiedByName = "getWorkers")
    @Mapping(target = "products", source = "products", qualifiedByName = "getProducts")
    public abstract PremiseDto toPremiseDto(Premise premise);

    @Mapping(target = "workers", source = "premise.workers")
    @Mapping(target = "owner",source = "premise.owner")
    @Mapping(target = "products", source = "premise.products")
    @Mapping(target = "id", source = "premise.id")
    @Mapping(target = "name", source = "premiseRequestDto.name")
    @Mapping(target = "address", source = "premiseRequestDto.address")
    @Mapping(target = "type", source = "premiseRequestDto.type")
    public abstract Premise toPremise(PremiseRequestDto premiseRequestDto, Premise premise);

    @Mapping(target = "workers", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "id", ignore = true)
    public abstract Premise toPremise(PremiseRequestDto premiseRequestDto);

    @Named("getWorkers")
    List<UserDto> getWorkers(Set<User> users) {
        return users.stream()
                .filter(user -> !Objects.equals(user.getRole(),UserRole.OWNER ))
                .map(userMapper::toUserDto)
                .toList();
    }

    @Named("getProducts")
    List<ProductDto> getProducts(List<Product> productList) {
        return UserServiceImpl.getList(productList.stream().map(productMapper::toProductDto).toList());
    }
}
