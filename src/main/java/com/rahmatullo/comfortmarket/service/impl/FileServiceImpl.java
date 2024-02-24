package com.rahmatullo.comfortmarket.service.impl;

import com.rahmatullo.comfortmarket.config.StorageProperties;
import com.rahmatullo.comfortmarket.entity.Product;
import com.rahmatullo.comfortmarket.service.FileService;
import com.rahmatullo.comfortmarket.service.ProductService;
import com.rahmatullo.comfortmarket.service.dto.ProductDto;
import com.rahmatullo.comfortmarket.service.enums.FileTypes;
import com.rahmatullo.comfortmarket.service.exception.EmptyFieldException;
import com.rahmatullo.comfortmarket.service.exception.FileUploadException;
import com.rahmatullo.comfortmarket.service.exception.NotFoundException;
import com.rahmatullo.comfortmarket.service.mapper.ProductMapper;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Objects;

@Service

@Slf4j
public class FileServiceImpl implements FileService {

    private static final String HOST_PHOTO =  "http://%s/products/assets/photos/%s%s";

    @Value("${cm.address}")
    private String address;

    private final ProductService productService;
    private final Path photoLocation;
    private final ProductMapper productMapper;

    public FileServiceImpl(ProductService productService, StorageProperties properties, ProductMapper productMapper) {
        this.productService = productService;
        photoLocation = Paths.get(properties.getPhotoLocation());
        this.productMapper = productMapper;
    }

    @Override
    public ProductDto uploadPhoto2Product(Long id, MultipartFile file) {
        log.info("Requested to upload photo to product {}", id);
        Product product = productService.toProduct(id);

        if(Objects.isNull(file)|| file.isEmpty()){
            log.warn("File is empty or null");
            throw new EmptyFieldException("File cannot be empty");
        }

        writeToFile(file, getPathLocation(product, file));

        product.setUrl(String.format(HOST_PHOTO, address, product.getId()+product.getBarcode(), getSuffix(file.getContentType())));

        log.info("Successfully uploaded and saved to product");
        return productMapper.toProductDto(product);
    }

    @Override
    public Resource loadPhoto(String filename) throws MalformedURLException {
        if(Objects.isNull(filename)|| StringUtils.isEmpty(filename)){
            log.warn("filename is empty");
            throw new NotFoundException("file is not found");
        }

        return new UrlResource(photoLocation.resolve(filename).toUri());
    }

    private void writeToFile(MultipartFile file, Path destinationFile) {
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFile,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.warn(e.getMessage());
            throw new FileUploadException("Failed to upload photo");
        }
    }

    private Path getPathLocation(Product product,  MultipartFile file) {
        String suffix = getSuffix(file.getContentType());
        Path destinationFile = this.photoLocation.resolve(
                        Paths.get(product.getId()+product.getBarcode()) + suffix)
                .normalize().toAbsolutePath();

        if (!destinationFile.getParent().equals(this.photoLocation.toAbsolutePath())) {
            log.warn("Cannot store file outside current directory.");
            throw new FileUploadException("Failed to upload photo");
        }
        return destinationFile;
    }

    private String getSuffix(String contentType) {
        FileTypes type = Arrays.stream(FileTypes.values()).filter(contentTypes -> contentTypes.getContentType().equals(contentType)).findAny().orElseThrow(() -> {
            log.warn("File content type does not match {} with jpeg and png", contentType);
            throw new RuntimeException("File content type does not match");
        });

        return type.getSuffix();
    }
}
