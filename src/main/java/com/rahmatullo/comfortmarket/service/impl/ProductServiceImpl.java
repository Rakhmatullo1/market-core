package com.rahmatullo.comfortmarket.service.impl;

import com.rahmatullo.comfortmarket.entity.Category;
import com.rahmatullo.comfortmarket.entity.Premise;
import com.rahmatullo.comfortmarket.entity.Product;
import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.repository.CategoryRepository;
import com.rahmatullo.comfortmarket.repository.PremiseRepository;
import com.rahmatullo.comfortmarket.repository.ProductRepository;
import com.rahmatullo.comfortmarket.service.AuthService;
import com.rahmatullo.comfortmarket.service.CategoryService;
import com.rahmatullo.comfortmarket.service.ProductService;
import com.rahmatullo.comfortmarket.service.dto.MessageDto;
import com.rahmatullo.comfortmarket.service.dto.ProductDto;
import com.rahmatullo.comfortmarket.service.dto.ProductRequestDto;
import com.rahmatullo.comfortmarket.service.enums.UserRole;
import com.rahmatullo.comfortmarket.service.exception.NotFoundException;
import com.rahmatullo.comfortmarket.service.mapper.CategoryMapper;
import com.rahmatullo.comfortmarket.service.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final PremiseRepository premiseRepository;
    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;
    private final CategoryService categoryService;
    private final AuthService authService;

    @Override
    public List<ProductDto> getProductsByCategoryId(Long categoryId, PageRequest pageRequest) {
        log.info("Requested to get all products by category");
        Category category = categoryService.toCategory(categoryId);

        User owner = checkAnGetOwner();

        log.info("Successfully fetched all product which are belonged to {} {}", owner.getUsername(), owner.getRole());
        return getProducts(productRepository.getAllByCategoryAndOwner(category, owner, pageRequest).getContent());
    }

    @Override
    public List<ProductDto> getProductsByPremiseId(Long premiseId, PageRequest pageRequest) {
        log.info("Requested to get products by premise {}", premiseId);
        Premise premise = toPremise(premiseId);
        return getProducts(productRepository.getAllByPremise(premise, pageRequest).getContent());
    }

    @Override
    public List<ProductDto> getProductByOwner(PageRequest pageRequest) {
        return getProducts(productRepository.getAllByOwner(checkAnGetOwner(), pageRequest).getContent());
    }

    @Override
    public ProductDto getById(Long id) {
        User owner = checkAnGetOwner();

        Product product = productRepository.findByIdAndOwner(id, owner).orElseThrow(()->new NotFoundException("Product is not found"));

        return productMapper.toProductDto(product);
    }

    @Override
    public ProductDto updateProduct(Long id, ProductRequestDto productRequestDto) {
        log.warn("Requested to update product with id {}", id);
        Product product = toProduct(id);

        if(!Objects.equals(product.getCategory().getId(), productRequestDto.getCategoryId())){
            removeProductFromCategory(product);
            Category category = categoryService.toCategory(productRequestDto.getCategoryId());
            product.setCategory(category);
            addProduct2Category( product);
        }

        product = productMapper.toProduct(productRequestDto, product);

        log.info("Successfully updated");
        return productMapper.toProductDto(productRepository.save(product));
    }

    @Override
    public ProductDto transfersProduct(Long id, Long premiseId) {
        log.info("Requested to transfer products to premise {}", premiseId);
        Product product = toProduct(id);

        Premise premise = toPremise(premiseId);

        removeProductsFromPremise(product);
        product.setPremise(premise);

        List<Product> productList = premise.getProducts();
        productList.add(product);
        premise.setProducts(productList);

        premiseRepository.save(premise);
        return productMapper.toProductDto(productRepository.save(product));
    }

    @Override
    public MessageDto deleteProduct(Long id) {
        log.info("Requested to delete product {}", id);
        Product product = toProduct(id);

        removeProductFromCategory( product);
        removeProductsFromPremise(product);
        productRepository.delete(product);
        log.info("Successfully deleted");
        return new MessageDto("Successfully deleted");
    }

    private List<ProductDto> getProducts(List<Product> products) {
        return products.stream().map(productMapper::toProductDto).toList();
    }

    @Override
    public Product toProduct(Long id){
        return productRepository.findById(id).orElseThrow(()->new NotFoundException("Product is not found"));
    }

    @Override
    public void addProduct2Category( Product product) {
        Category category = categoryService.toCategory(product.getCategory().getId());

        List<Product> products =category.getProductList();
        products.add(product);

        categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public void removeProductsFromPremise(Product product) {
        log.info("Requested to remove product  {}  from {}", product.getName(), product.getPremise().getId());
        Premise premise = toPremise(product.getPremise().getId());

        List<Product> products = premise.getProducts();
        products.remove(product);
        premise.setProducts(products);

        premiseRepository.save(premise);
        log.info("successfully deleted");
    }

    @Override
    public void removeProductFromCategory( Product product) {
        log.info("Requested to remove {} from category {}", product.getName(), product.getCategory().getId());
        Category category = categoryService.toCategory(product.getCategory().getId());

        List<Product> products = category.getProductList();
        products.remove(product);
        category.setProductList(products);

        categoryRepository.save(category);
        log.info("Successfully deleted");
    }

    public Premise toPremise(Long id) {
        return premiseRepository.findById(id).orElseThrow(()->{
            log.warn("premise is not found");
            throw new NotFoundException("Premise is not found");
        });
    }

    private User checkAnGetOwner() {
        User owner = authService.getUser();

        if(!Objects.equals(owner.getRole(), UserRole.OWNER)){
            owner = owner.getOwner();
        }
        return owner;
    }
}
