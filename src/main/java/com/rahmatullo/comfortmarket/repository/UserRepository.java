package com.rahmatullo.comfortmarket.repository;

import com.rahmatullo.comfortmarket.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    Page<User> findByRoleIn(List<String> user, Pageable pageable);
}
