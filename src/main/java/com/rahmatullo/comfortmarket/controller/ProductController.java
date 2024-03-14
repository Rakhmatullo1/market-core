package com.rahmatullo.comfortmarket.controller;

import com.rahmatullo.comfortmarket.service.AuthService;
import com.rahmatullo.comfortmarket.service.FileService;
import com.rahmatullo.comfortmarket.service.HistoryService;
import com.rahmatullo.comfortmarket.service.ProductService;
import com.rahmatullo.comfortmarket.service.dto.*;
import com.rahmatullo.comfortmarket.service.enums.Action4Product;
import com.rahmatullo.comfortmarket.service.mapper.ProductMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final FileService fileService;
    private final HistoryService historyService;
    private final AuthService authService;
    private final ProductMapper productMapper;

    @GetMapping("/by-category/{id}")
    public ResponseEntity<List<ProductDto>> getProductsCategory(@PathVariable Long id, @RequestParam(required = false, defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "10") int size ) {
        return ResponseEntity.ok(productService.getProductsByCategoryId(id, PageRequest.of(page, size)));
    }

    @GetMapping("/by-premise/{id}")
    public ResponseEntity<List<ProductDto>> getProductsPremise(@PathVariable Long id, @RequestParam(required = false, defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "10") int size ) {
        return ResponseEntity.ok(productService.getProductsByPremiseId(id, PageRequest.of(page, size)));
    }

    @GetMapping()
    public ResponseEntity<List<ProductDto>> getProductsPremise(@RequestParam(required = false, defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "10") int size) {
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

    @GetMapping("/history/{id}")
    public ResponseEntity<List<HistoryDto>> getHistory(@PathVariable Long id) {
        return ResponseEntity.ok(historyService.getHistory(id));
    }

    @PostMapping("/upload-file/{id}")
    public ResponseEntity<ProductDto> uploadFile(@RequestParam MultipartFile file, @PathVariable Long id) {
        return ResponseEntity.ok(fileService.uploadPhoto2Product(id, file));
    }

    @PostMapping("/upload_file/to/products")
    public ResponseEntity<MessageDto> convertXSLFile(@RequestParam MultipartFile file) {
        return ResponseEntity.ok(productService.convertXLSFile2Products(file));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id,@Valid @RequestBody ProductRequestDto productRequestDto, @RequestParam Long premiseId){

        Map<Object, Object> details = new HashMap<>();

        ProductDto oldProductDto = toProductDto(id);

        ProductDto productDto = productService.updateProduct(id, productRequestDto, premiseId);

        details.put("action",Action4Product.UPDATED);
        details.put("description", String.format("%s product updated to premise %s \n [%s] \n old version: [%s] ", id, premiseId, productDto.toString(),oldProductDto.toString()));
        details.put("id", id);

        return ResponseEntity.ok(historyService.createHistory(productDto,details));
    }

    @PutMapping("/transfer/part/{id}")
    public ResponseEntity<ProductDto> transferProductPartly(@PathVariable Long id , @RequestBody ProductTransferDto productTransferDto) {

        Map<Object, Object> details = new HashMap<>();
        ProductDto productDto = toProductDto(id);
        details.put("action",Action4Product.TRANSFERRED);
        details.put("description", String.format("%s, %s product transferred to premise %s from %s, count: %s", productDto.getName(),productDto.getBarcode(), productTransferDto.getDestinationId(), productTransferDto.getPreviousId(), productTransferDto.getCount()));
        details.put("id", id);

        return ResponseEntity.ok(historyService.createHistory(productService.transfersProductPartly(id, productTransferDto), details));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageDto> deleteProduct(@PathVariable Long id, @RequestParam Long premiseId){
        ProductDto productDto = toProductDto(id);
        Map<Object, Object> details = new HashMap<>();

        details.put("action",Action4Product.DELETED);
        details.put("description", String.format("%s, %s product deleted on premise %s",productDto.getName() ,productDto.getBarcode() , premiseId));
        details.put("id", id);

        return ResponseEntity.ok(historyService.createHistory(productService.deleteProduct(id, premiseId), details) );
    }

    @DeleteMapping("/all/{id}")
    public ResponseEntity<MessageDto> deleteProduct(@PathVariable Long id) {
        historyService.getHistory(((long) id)).forEach(value->value.setProduct(null));
        ProductDto productDto = toProductDto(id);

        Map<Object, Object> details = new HashMap<>();

        details.put("action",Action4Product.DELETED);
        details.put("description", String.format("%s, %s product deleted fully", productDto.getName(), productDto.getBarcode()));
        details.put("id", id);

        return ResponseEntity.ok(historyService.createHistory(productService.deleteProduct(id), details));
    }

    private ProductDto toProductDto(Long id) {
        return productMapper.toProductDto(productService.toProduct(id, authService.getOwner()));
    }
}
