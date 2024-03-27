package com.rahmatullo.comfortmarket.service.impl;

import com.rahmatullo.comfortmarket.entity.Category;
import com.rahmatullo.comfortmarket.entity.Premise;
import com.rahmatullo.comfortmarket.entity.Product;
import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.repository.CategoryRepository;
import com.rahmatullo.comfortmarket.repository.PremiseRepository;
import com.rahmatullo.comfortmarket.repository.ProductRepository;
import com.rahmatullo.comfortmarket.service.CategoryService;
import com.rahmatullo.comfortmarket.service.ProductService;
import com.rahmatullo.comfortmarket.service.dto.MessageDto;
import com.rahmatullo.comfortmarket.service.dto.ProductDto;
import com.rahmatullo.comfortmarket.service.exception.NotFoundException;
import com.rahmatullo.comfortmarket.service.mapper.ProductMapper;
import com.rahmatullo.comfortmarket.service.utils.AuthUtils;
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
    private final CategoryService categoryService;
    private final AuthUtils authUtils;

    @Override
    public List<ProductDto> getProductsByCategoryId(Long categoryId, PageRequest pageRequest) {
        log.info("Requested to get all products by category");
        Category category = categoryService.toCategory(categoryId);

        User owner = authUtils.getOwner();

        log.info("Successfully fetched all product which are belonged to {} {}", owner.getUsername(), owner.getRole());
        return getProducts(productRepository.getAllByCategoryAndOwner(category, owner, pageRequest).getContent());
    }

    @Override
    public List<ProductDto> getProductsByPremiseId(Long premiseId, PageRequest pageRequest) {
        log.info("Requested to get products by premise {}", premiseId);
        Premise premise = toPremise(premiseId);
        List<ProductDto> products = getProducts(productRepository.getAllByPremise(premise, pageRequest).getContent());
        products.forEach(p->
                p.setExtra(p.getExtra().stream().filter(e->Objects.equals(e.getPremise(), premise.getName())).toList()));
        return products;
    }

    @Override
    public List<ProductDto> getProductByOwner(PageRequest pageRequest) {
        log.info("Requested to get all products");
        return getProducts(productRepository.getAllByOwner(authUtils.getOwner(), pageRequest).getContent());
    }

    @Override
    public ProductDto getById(Long id) {
        log.info("Requested to get product by id");
        User owner = authUtils.getOwner();

        Product product = productRepository.findByIdAndOwner(id, owner).orElseThrow(()->new NotFoundException("Product is not found"));

        return productMapper.toProductDto(product);
    }

    @Override
    public MessageDto deleteProduct(Long id) {
        Product product = toProduct(id, authUtils.getOwner());

        removeProductFromCategory(product);
        product.setOwner(null);

        productRepository.delete(productRepository.save(product));
        return new MessageDto("Successfully deleted");
    }

    @Override
    public Product toProduct(Long id, User owner) {
        return productRepository.findByIdAndOwner(id, owner).orElseThrow(()->new NotFoundException("the product has not found"));
    }

    private List<ProductDto> getProducts(List<Product> products) {
        return products.stream().map(productMapper::toProductDto).toList();
    }

    private Premise toPremise(Long id) {
        return premiseRepository.findByOwnerAndId( authUtils.getOwner(), id).orElseThrow(()->{
            log.warn("premise is not found");
            throw new NotFoundException("Premise is not found");
        });
    }

    private void removeProductFromCategory( Product product) {
        log.info("Requested to remove {} from category {}", product.getName(), product.getCategory().getId());
        Category category = categoryService.toCategory(product.getCategory().getId());

        List<Product> products = category.getProductList();
        products.remove(product);
        category.setProductList(products);

        categoryRepository.save(category);
        log.info("Successfully deleted");
    }
}
