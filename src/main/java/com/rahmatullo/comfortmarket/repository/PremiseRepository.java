package com.rahmatullo.comfortmarket.repository;

import com.rahmatullo.comfortmarket.entity.Premise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PremiseRepository extends JpaRepository<Premise, Long> {
}
