package com.rahmatullo.comfortmarket.service.impl;

import com.rahmatullo.comfortmarket.entity.Category;
import com.rahmatullo.comfortmarket.entity.Product;
import com.rahmatullo.comfortmarket.repository.CategoryRepository;
import com.rahmatullo.comfortmarket.service.CategoryService;
import com.rahmatullo.comfortmarket.service.dto.CategoryDto;
import com.rahmatullo.comfortmarket.service.dto.CategoryRequestDto;
import com.rahmatullo.comfortmarket.service.exception.NotFoundException;
import com.rahmatullo.comfortmarket.service.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDto addCategory(CategoryRequestDto categoryRequestDto) {
        log.info("Requested to add new category ");
        Category category = categoryMapper.toCategory(categoryRequestDto);
        log.info("Successfully added category");
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> findAllCategory() {
        return categoryRepository
                .findAll()
                .stream()
                .map(categoryMapper::toCategoryDto)
                .toList();
    }

    @Override
    public void addProduct2Category( Product product) {
        Category category = toCategory(product.getCategory().getId());

        List<Product> products =category.getProductList();
        products.add(product);

        categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public void removeProductFromCategory( Product product) {
        log.info("Requested to remove {} from category {}", product, product.getCategory().getId());
        Category category = toCategory(product.getCategory().getId());

        List<Product> products = category.getProductList();
        products.remove(product);
        category.setProductList(products);

        categoryRepository.save(category);
        log.info("Successfully deleted");
    }

    @Override
    public Category toCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(()->new NotFoundException("Category is not found"));
    }
}
