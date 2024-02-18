package com.rahmatullo.comfortmarket.repository;

import com.rahmatullo.comfortmarket.entity.Category;
import com.rahmatullo.comfortmarket.entity.Premise;
import com.rahmatullo.comfortmarket.entity.Product;
import com.rahmatullo.comfortmarket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByBarcode(String barcode);

    List<Product> getAllByOwner(User user);

    List<Product> getAllByCategory(Category category);

    List<Product> getAllByPremise(Premise premise);
}
