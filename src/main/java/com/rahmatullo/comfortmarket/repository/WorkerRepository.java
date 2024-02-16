package com.rahmatullo.comfortmarket.repository;

import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, Long> {
    Optional<Worker> getWorkerByUser(User user);
}
