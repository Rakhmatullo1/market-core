package com.rahmatullo.comfortmarket.controller;

import com.rahmatullo.comfortmarket.service.ProductInfoService;
import com.rahmatullo.comfortmarket.service.dto.ProductInfoDto;
import com.rahmatullo.comfortmarket.service.dto.request.ProductInfoRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product_info")
@RequiredArgsConstructor
public class ProductInfoController {
    private final ProductInfoService productInfoService;

    @GetMapping
    public ResponseEntity<List<ProductInfoDto>> findAll(@RequestParam(required = false, defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(productInfoService.getAll(PageRequest.of(page, size)));
    }

    @PostMapping
    public ResponseEntity<ProductInfoDto> create(@RequestBody ProductInfoRequestDto productRequestDto) {
        return ResponseEntity.ok(productInfoService.create(productRequestDto));
    }
}
