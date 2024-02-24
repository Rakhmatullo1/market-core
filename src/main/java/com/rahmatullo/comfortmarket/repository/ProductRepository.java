package com.rahmatullo.comfortmarket.repository;

import com.rahmatullo.comfortmarket.entity.Category;
import com.rahmatullo.comfortmarket.entity.Premise;
import com.rahmatullo.comfortmarket.entity.Product;
import com.rahmatullo.comfortmarket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByBarcode(String barcode);

    List<Product> getAllByOwner(User user);

    List<Product> getAllByCategoryAndOwner(Category category, User owner);

    List<Product> getAllByPremise(Premise premise);

    Optional<Product> findByIdAndOwner(Long id, User owner);
}
