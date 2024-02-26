package com.rahmatullo.comfortmarket.service;

import com.rahmatullo.comfortmarket.entity.Product;
import com.rahmatullo.comfortmarket.service.dto.MessageDto;
import com.rahmatullo.comfortmarket.service.dto.PremiseDto;
import com.rahmatullo.comfortmarket.service.dto.ProductDto;
import com.rahmatullo.comfortmarket.service.dto.ProductRequestDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    List<ProductDto> getProductsByCategoryId(Long categoryId,  PageRequest pageRequest);

    List<ProductDto> getProductsByPremiseId(Long premiseId, PageRequest pageRequest);

    List<ProductDto> getProductByOwner(PageRequest pageRequest);

    ProductDto getById(Long id);

    ProductDto updateProduct(Long id, ProductRequestDto productRequestDto);

    ProductDto transfersProduct(Long id, Long premiseId);

    MessageDto deleteProduct(Long id);

    MessageDto convertXLSFile2Products(MultipartFile file);

    Product toProduct(Long id);

    void addProduct2Category( Product product);

    void removeProductsFromPremise(Product product);

    void removeProductFromCategory( Product product);

    PremiseDto addProductsToPremise(Long id, ProductRequestDto productRequestDto);
}
