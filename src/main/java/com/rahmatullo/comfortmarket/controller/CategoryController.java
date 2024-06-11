package com.rahmatullo.comfortmarket.controller;

import com.rahmatullo.comfortmarket.service.CategoryService;
import com.rahmatullo.comfortmarket.service.dto.CategoryDto;
import com.rahmatullo.comfortmarket.service.dto.request.CategoryRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDto> add(@Valid @RequestBody CategoryRequestDto categoryRequestDto) {
        return ResponseEntity.ok(categoryService.addCategory(categoryRequestDto));
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAll(@RequestParam(required = false, defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(categoryService.findAllCategory(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> update(@PathVariable Long id, @RequestParam(name = "name") String name){
        return ResponseEntity.ok(categoryService.updateCategory(id, name));
    }
}
