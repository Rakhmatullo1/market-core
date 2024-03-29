package com.rahmatullo.comfortmarket.repository;

import com.rahmatullo.comfortmarket.entity.ProductInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductInfoRepository  extends JpaRepository<ProductInfo, Long> {
    boolean existsByBarcode(String barcode);

    Optional<ProductInfo> findByBarcode(String barcode);
}
