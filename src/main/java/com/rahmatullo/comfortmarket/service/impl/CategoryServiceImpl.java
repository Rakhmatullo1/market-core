package com.rahmatullo.comfortmarket.service.impl;

import com.rahmatullo.comfortmarket.entity.Category;
import com.rahmatullo.comfortmarket.repository.CategoryRepository;
import com.rahmatullo.comfortmarket.service.CategoryService;
import com.rahmatullo.comfortmarket.service.dto.CategoryDto;
import com.rahmatullo.comfortmarket.service.dto.CategoryRequestDto;
import com.rahmatullo.comfortmarket.service.exception.ExistsException;
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

        if(categoryRepository.existsByName(categoryRequestDto.getName())){
            log.warn("The category exists");
            throw new ExistsException("Category exists " + categoryRequestDto.getName());
        }

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
    public CategoryDto findById(Long id) {
        return categoryMapper.toCategoryDto(toCategory(id));
    }

    @Override
    public Category toCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(()->new NotFoundException("Category is not found"));
    }
}
