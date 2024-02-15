package com.rahmatullo.comfortmarket.repository;

import com.rahmatullo.comfortmarket.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, Long> {
}
