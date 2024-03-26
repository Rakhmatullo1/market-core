package com.rahmatullo.comfortmarket.entity;

import com.rahmatullo.comfortmarket.service.enums.Action;
import com.rahmatullo.comfortmarket.service.enums.InvoiceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date date;
    @OneToMany
    private Set<ProductDetails> productDetailsSet;
    private String description;
    @ManyToOne
    private Premise premise;
    private Double overallInitialPrice;
    private Double overallFinalPrice;
    @ManyToOne
    private User toUser;
    private String createdBy;
    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;
    @Enumerated(EnumType.STRING)
    private Action action;
    private Long previousId;
}
