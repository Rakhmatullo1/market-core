package com.rahmatullo.comfortmarket.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String barcode;
    private Date createdAt;
    private String article;
    @ElementCollection
    private List<String> count=new ArrayList<>();
    private Double price;
    private String url;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id")
    private User owner;
    private String addedBy;
    @ManyToMany
    @JoinTable(name = "premise_products",
            joinColumns = @JoinColumn(name = "products_id"),
            inverseJoinColumns = @JoinColumn(name = "premise_id"))
    private Set<Premise> premise = new HashSet<>();
}
