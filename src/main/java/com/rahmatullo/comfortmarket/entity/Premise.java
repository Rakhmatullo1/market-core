package com.rahmatullo.comfortmarket.entity;

import com.rahmatullo.comfortmarket.service.enums.PremiseType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Premise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(value = EnumType.STRING)
    private PremiseType type;
    private String address;
    private String name;
    @OneToMany
    private List<Product> products = new ArrayList<>();
    @ManyToOne
    private User owner ;
    @ManyToMany(mappedBy = "premise")
    private Set<User> workers = new HashSet<>();
}
