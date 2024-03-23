package com.rahmatullo.comfortmarket.repository;

import com.rahmatullo.comfortmarket.entity.ProductInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductInfoRepository  extends JpaRepository<ProductInfo, Long> {

    List<ProductInfo> getAll(Pageable pageable);
}
