package com.rahmatullo.comfortmarket.controller;

import com.rahmatullo.comfortmarket.service.FileService;
import com.rahmatullo.comfortmarket.service.ProductInfoService;
import com.rahmatullo.comfortmarket.service.ProductService;
import com.rahmatullo.comfortmarket.service.dto.MessageDto;
import com.rahmatullo.comfortmarket.service.dto.ProductDto;
import com.rahmatullo.comfortmarket.service.dto.ProductInfoDto;
import com.rahmatullo.comfortmarket.service.dto.request.ProductInfoRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductInfoService productInfoService;
    private final FileService fileService;

    @GetMapping("/by-category/{id}")
    public ResponseEntity<List<ProductDto>> getProductsCategory(@PathVariable Long id, @RequestParam(required = false, defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "10") int size ) {
        return ResponseEntity.ok(productService.getProductsByCategoryId(id, PageRequest.of(page, size)));
    }

    @GetMapping("/by-premise/{id}")
    public ResponseEntity<List<ProductDto>> getProductsPremise(@PathVariable Long id, @RequestParam(required = false, defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "10") int size ) {
        return ResponseEntity.ok(productService.getProductsByPremiseId(id, PageRequest.of(page, size)));
    }

    @GetMapping()
    public ResponseEntity<List<ProductDto>> getProductsOwner(@RequestParam(required = false, defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.getProductByOwner(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id){
        return ResponseEntity.ok(productService.getById(id));
    }

    @GetMapping("/assets/photos/{name}")
    public ResponseEntity<Resource> load(@PathVariable String name) throws MalformedURLException {
        return ResponseEntity
                .ok().header("Content-Type", "image/jpeg", "image/png").body(fileService.loadPhoto(name));
    }

    @PostMapping()
    public ResponseEntity<ProductInfoDto> create(@RequestBody ProductInfoRequestDto productInfoRequestDto) {
        return ResponseEntity.ok(productInfoService.create(productInfoRequestDto));
    }

    @PostMapping("/upload-file/{id}")
    public ResponseEntity<ProductDto> uploadFile(@RequestParam MultipartFile file, @PathVariable Long id) {
        return ResponseEntity.ok(fileService.uploadPhoto2Product(id, file));
    }

    @DeleteMapping("/all/{id}")
    public ResponseEntity<MessageDto> deleteProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.deleteProduct(id));
    }
}
