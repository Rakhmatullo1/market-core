package com.rahmatullo.comfortmarket.service;

import com.rahmatullo.comfortmarket.entity.Product;
import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.service.dto.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    List<ProductDto> getProductsByCategoryId(Long categoryId,  PageRequest pageRequest);

    List<ProductDto> getProductsByPremiseId(Long premiseId, PageRequest pageRequest);

    List<ProductDto> getProductByOwner(PageRequest pageRequest);

    ProductDto getById(Long id);

    MessageDto convertXLSFile2Products(MultipartFile file);

    PremiseDto addProductsToPremise(Long id, ProductRequestDto productRequestDto);

    ProductDto updateProduct(Long id, ProductRequestDto productRequestDto, Long premiseId);

    ProductDto  transfersProductPartly(Long id, ProductTransferDto productTransferDto);

    MessageDto deleteProduct(Long id, Long premiseId);

    MessageDto deleteProduct(Long id);

    Product toProduct(Long id, User owner);
}
