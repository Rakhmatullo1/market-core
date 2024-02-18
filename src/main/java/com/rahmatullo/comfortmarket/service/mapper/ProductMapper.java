package com.rahmatullo.comfortmarket.service.mapper;

import com.rahmatullo.comfortmarket.entity.*;
import com.rahmatullo.comfortmarket.repository.CategoryRepository;
import com.rahmatullo.comfortmarket.repository.WorkerRepository;
import com.rahmatullo.comfortmarket.service.dto.ProductDto;
import com.rahmatullo.comfortmarket.service.dto.ProductRequestDto;
import com.rahmatullo.comfortmarket.service.enums.UserRole;
import com.rahmatullo.comfortmarket.service.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Objects;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR, componentModel = "spring")
@Slf4j
public abstract  class ProductMapper {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private WorkerRepository workerRepository;

    @Mapping(target = "category", expression = "java(product.getCategory().getName())")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "date")
    public abstract ProductDto toProductDto(Product product);

    @Mapping(target = "createdAt", expression = "java(getCreatedTime())")
    @Mapping(target = "premise", source = "premise")
    @Mapping(target = "owner", expression = "java(getOwner(user))")
    @Mapping(target = "category", source = "productRequestDto.categoryId", qualifiedByName = "getCategory")
    @Mapping(target = "addedBy", expression = "java(addedBy(user))")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "productRequestDto.name")
    public abstract Product toProduct(ProductRequestDto productRequestDto, Premise premise, User user);

    @Named("addedBy")
    String addedBy(User user) {
        return user.getUsername();
    }

    @Named("getOwner")
    User getOwner(User user) {
        if(Objects.equals(user.getRole(), UserRole.OWNER)){
            return user;
        }
        Worker worker  = workerRepository.getWorkerByUser(user).orElseThrow(()->{
            log.warn("Worker is not found");
            throw new NotFoundException("Worker is not found");
        });
        return worker.getOwner();
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
}
