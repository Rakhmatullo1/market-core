package com.rahmatullo.comfortmarket.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Product {
    @Id
    private Long id;
    private String name;
    private String barcode;
    private Date createdAt;
    private int count;
    private Double price;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    private String addedBy;
    @OneToOne
    private Premise premise;
}
