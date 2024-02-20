package com.rahmatullo.comfortmarket.controller;

import com.rahmatullo.comfortmarket.service.ProductService;
import com.rahmatullo.comfortmarket.service.dto.MessageDto;
import com.rahmatullo.comfortmarket.service.dto.ProductDto;
import com.rahmatullo.comfortmarket.service.dto.ProductRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/by-category/{id}")
    public ResponseEntity<List<ProductDto>> getProductsCategory(@PathVariable Long id ) {
        return ResponseEntity.ok(productService.getProductsByCategoryId(id));
    }

    @GetMapping("/by-premise/{id}")
    public ResponseEntity<List<ProductDto>> getProductsPremise(@PathVariable Long id ) {
        return ResponseEntity.ok(productService.getProductsByPremiseId(id));
    }

    @GetMapping()
    public ResponseEntity<List<ProductDto>> getProductsPremise() {
        return ResponseEntity.ok(productService.getProductByOwner());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id,@Valid @RequestBody ProductRequestDto productRequestDto){
        return ResponseEntity.ok(productService.updateProduct(id, productRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageDto> deleteProduct(@PathVariable Long id){
        return ResponseEntity.ok(productService.deleteProduct(id));
    }
}
