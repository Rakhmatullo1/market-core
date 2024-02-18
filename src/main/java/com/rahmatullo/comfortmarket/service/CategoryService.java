package com.rahmatullo.comfortmarket.service;

import com.rahmatullo.comfortmarket.entity.Product;
import com.rahmatullo.comfortmarket.service.dto.CategoryDto;
import com.rahmatullo.comfortmarket.service.dto.CategoryRequestDto;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(CategoryRequestDto categoryRequestDto);

    List<CategoryDto> findAllCategory();
}
