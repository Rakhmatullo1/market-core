package com.rahmatullo.comfortmarket.repository;

import com.rahmatullo.comfortmarket.entity.ProductProposal;
import com.rahmatullo.comfortmarket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProposalRepository extends JpaRepository<ProductProposal, Long> {
    List<ProductProposal> findByToUser(User user);

    Optional<ProductProposal> findByToUserAndId(User user, Long id);
}
