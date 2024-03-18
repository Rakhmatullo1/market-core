package com.rahmatullo.comfortmarket.repository;


import com.rahmatullo.comfortmarket.entity.History;
import com.rahmatullo.comfortmarket.entity.Product;
import com.rahmatullo.comfortmarket.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    Page<History> getAllByProductAndUser(Product product, User user, Pageable pageable);

    List<History> getByProduct(Product product);
}
