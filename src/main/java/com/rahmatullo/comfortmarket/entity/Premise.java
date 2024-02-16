package com.rahmatullo.comfortmarket.entity;

import com.rahmatullo.comfortmarket.service.enums.PremiseType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
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
    @OneToOne
    private User owner;
}
