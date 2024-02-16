package com.rahmatullo.comfortmarket.service.mapper;

import com.rahmatullo.comfortmarket.entity.Category;
import com.rahmatullo.comfortmarket.entity.Product;
import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.entity.Worker;
import com.rahmatullo.comfortmarket.repository.CategoryRepository;
import com.rahmatullo.comfortmarket.repository.WorkerRepository;
import com.rahmatullo.comfortmarket.service.AuthService;
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

import java.util.Objects;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR, componentModel = "spring")
@Slf4j
public abstract  class ProductMapper {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private WorkerRepository workerRepository;

    @Mapping(target = "category", expression = "java(product.getCategory().getName())")
    public abstract ProductDto toProductDto(Product product);

    @Mapping(target = "owner", expression = "java(getOwner())")
    @Mapping(target = "category", source = "productRequestDto.categoryId", qualifiedByName = "getCategory")
    @Mapping(target = "addedBy", expression = "java(addedBy())")
    @Mapping(target = "id", ignore = true)
    public abstract Product toProduct(ProductRequestDto productRequestDto);

    @Named("addedBy")
    String addedBy() {
        return getUser().getUsername();
    }

    @Named("getOwner")
    User getOwner() {
        if(Objects.equals(getUser().getRole(), UserRole.OWNER)){
            return getUser();
        }
        Worker worker  = workerRepository.getWorkerByUser(getUser()).orElseThrow(()->{
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

    private User getUser() {
        return authService.getUser();
    }
}
