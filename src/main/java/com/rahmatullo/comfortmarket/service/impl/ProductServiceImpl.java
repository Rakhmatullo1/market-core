package com.rahmatullo.comfortmarket.service.impl;

import com.rahmatullo.comfortmarket.entity.*;
import com.rahmatullo.comfortmarket.repository.CategoryRepository;
import com.rahmatullo.comfortmarket.repository.PremiseRepository;
import com.rahmatullo.comfortmarket.repository.ProductRepository;
import com.rahmatullo.comfortmarket.repository.WorkerRepository;
import com.rahmatullo.comfortmarket.service.AuthService;
import com.rahmatullo.comfortmarket.service.ProductService;
import com.rahmatullo.comfortmarket.service.dto.ProductDto;
import com.rahmatullo.comfortmarket.service.enums.UserRole;
import com.rahmatullo.comfortmarket.service.exception.DoesNotMatchException;
import com.rahmatullo.comfortmarket.service.exception.NotFoundException;
import com.rahmatullo.comfortmarket.service.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final AuthService authService;
    private final PremiseRepository premiseRepository;
    private final WorkerRepository workerRepository;

    @Override
    public List<ProductDto> getProductsByCategoryId(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(()->new NotFoundException("Category is not found"));
        return getProducts(productRepository.getAllByCategory(category));
    }

    @Override
    public List<ProductDto> getProductsByPremiseId(Long premiseId) {
        Premise premise = premiseRepository.findById(premiseId).orElseThrow(()->new NotFoundException("Premise is not found"));
        return getProducts(productRepository.getAllByPremise(premise));
    }

    @Override
    public List<ProductDto> getProductByOwner() {
        User user =  authService.getUser();

        if(Objects.equals(user.getRole(), UserRole.OWNER)){
            return getProducts(productRepository.getAllByOwner(user));
        }

        Worker worker = workerRepository.getWorkerByUser(user).orElseThrow(()->{
           throw new NotFoundException("user is not found");
        });

        if(Objects.isNull(worker.getOwner())) {
            throw new DoesNotMatchException("You have not been enabled yet");
        }

        return getProducts(productRepository.getAllByOwner(worker.getOwner()));
    }

    private List<ProductDto> getProducts(List<Product> products) {
        return products.stream().map(productMapper::toProductDto).toList();
    }
}
