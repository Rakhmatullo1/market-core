package com.rahmatullo.comfortmarket.repository;

import com.rahmatullo.comfortmarket.entity.Category;
import com.rahmatullo.comfortmarket.entity.Premise;
import com.rahmatullo.comfortmarket.entity.Product;
import com.rahmatullo.comfortmarket.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByBarcode(String barcode);

    Page<Product> getAllByOwner(User user, Pageable pageable);

    Page<Product> getAllByCategoryAndOwner(Category category, User owner, Pageable pageable);

    Page<Product> getAllByPremise(Premise premise, Pageable pageable);

    Optional<Product> findByIdAndOwner(Long id, User owner);

    Optional<Product> findByBarcodeAndOwner(String barcode, User user);

    Optional<Product> findByPremiseAndBarcode(Premise premise, String barcode);
}
