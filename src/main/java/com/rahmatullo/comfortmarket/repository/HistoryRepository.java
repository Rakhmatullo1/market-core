package com.rahmatullo.comfortmarket.repository;


import com.rahmatullo.comfortmarket.entity.History;
import com.rahmatullo.comfortmarket.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    List<History> getAllByProduct(Product product);
}
