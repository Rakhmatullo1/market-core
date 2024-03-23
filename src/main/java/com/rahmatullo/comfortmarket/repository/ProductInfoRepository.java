package com.rahmatullo.comfortmarket.repository;

import com.rahmatullo.comfortmarket.entity.ProductInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductInfoRepository  extends JpaRepository<ProductInfo, Long> {
}
