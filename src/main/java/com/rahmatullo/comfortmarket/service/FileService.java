package com.rahmatullo.comfortmarket.service;

import com.rahmatullo.comfortmarket.service.dto.ProductDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;

public interface FileService {
    ProductDto uploadPhoto2Product(Long id, MultipartFile file);

    Resource loadPhoto(String filename) throws MalformedURLException;
}
