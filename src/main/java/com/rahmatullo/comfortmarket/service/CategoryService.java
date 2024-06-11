package com.rahmatullo.comfortmarket.service;

import com.rahmatullo.comfortmarket.entity.Category;
import com.rahmatullo.comfortmarket.service.dto.CategoryDto;
import com.rahmatullo.comfortmarket.service.dto.request.CategoryRequestDto;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(CategoryRequestDto categoryRequestDto);

    List<CategoryDto> findAllCategory(PageRequest pageRequest);

    CategoryDto findById(Long id);

    Category toCategory(Long id);

    CategoryDto updateCategory(Long id, String name);
}
