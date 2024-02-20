package com.rahmatullo.comfortmarket.service.impl;

import com.rahmatullo.comfortmarket.entity.*;
import com.rahmatullo.comfortmarket.repository.ProductRepository;
import com.rahmatullo.comfortmarket.repository.WorkerRepository;
import com.rahmatullo.comfortmarket.service.AuthService;
import com.rahmatullo.comfortmarket.service.CategoryService;
import com.rahmatullo.comfortmarket.service.PremiseService;
import com.rahmatullo.comfortmarket.service.ProductService;
import com.rahmatullo.comfortmarket.service.dto.MessageDto;
import com.rahmatullo.comfortmarket.service.dto.ProductDto;
import com.rahmatullo.comfortmarket.service.dto.ProductRequestDto;
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
    private final CategoryService categoryService;
    private final AuthService authService;
    private final PremiseService premiseService;
    private final WorkerRepository workerRepository;

    @Override
    public List<ProductDto> getProductsByCategoryId(Long categoryId) {
        Category category = categoryService.toCategory(categoryId);
        return getProducts(productRepository.getAllByCategory(category));
    }

    @Override
    public List<ProductDto> getProductsByPremiseId(Long premiseId) {
        Premise premise = premiseService.toPremise(premiseId);
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

    @Override
    public ProductDto updateProduct(Long id, ProductRequestDto productRequestDto) {
        log.warn("Requested to update product with id {}", id);
        Product product = toProduct(id);

        categoryService.removeProductFromCategory( product);

        Category category = categoryService.toCategory(productRequestDto.getCategoryId());

        product = productMapper.toProduct(productRequestDto, product);
        product.setCategory(category);

        categoryService.addProduct2Category( product);

        log.info("Successfully updated");
        return productMapper.toProductDto(productRepository.save(product));
    }

    @Override
    public MessageDto deleteProduct(Long id) {
        log.info("Requested to delete product {}", id);
        Product product = toProduct(id);

        categoryService.removeProductFromCategory( product);
        premiseService.removeProductsFromPremise(product);
        productRepository.delete(product);
        log.info("Successfully deleted");
        return new MessageDto("Successfully deleted");
    }

    private List<ProductDto> getProducts(List<Product> products) {
        return products.stream().map(productMapper::toProductDto).toList();
    }

    private Product toProduct(Long id){
        return productRepository.findById(id).orElseThrow(()->new NotFoundException("Product is not found"));
    }
}
