package com.rahmatullo.comfortmarket.repository;

import com.rahmatullo.comfortmarket.entity.Invoice;
import com.rahmatullo.comfortmarket.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Page<Invoice> findAllByToUser(User owner, Pageable pageable);

    Optional<Invoice> findByIdAndToUser( Long id, User user);
}
