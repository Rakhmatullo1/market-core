package com.rahmatullo.comfortmarket.repository;

import com.rahmatullo.comfortmarket.entity.Premise;
import com.rahmatullo.comfortmarket.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PremiseRepository extends JpaRepository<Premise, Long> {
    boolean existsByOwnerAndId(User owner, Long id);

    Page<Premise> findAllByOwner(User user, Pageable pageable);
}
