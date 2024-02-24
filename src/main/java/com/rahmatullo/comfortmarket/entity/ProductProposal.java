package com.rahmatullo.comfortmarket.entity;

import com.rahmatullo.comfortmarket.service.enums.Action;
import com.rahmatullo.comfortmarket.service.enums.ProposalStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ProductProposal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private ProposalStatus status;
    private Date createdAt;
    private String createdBy;
    private String name;
    @Column(name = "description", length = 1000)
    private String description;
    private Action action;
    private Long count;
    private Double sellAmount;
    @ManyToOne
    private Product product;
    private String approvedBy;
}
