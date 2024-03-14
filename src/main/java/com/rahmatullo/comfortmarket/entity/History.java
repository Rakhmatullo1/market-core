package com.rahmatullo.comfortmarket.entity;

import com.rahmatullo.comfortmarket.service.enums.Action4Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "history")
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Product product;
    @Column(length = 10240)
    private String description;
    private Date createdAt;
    @Enumerated(EnumType.STRING)
    private Action4Product action;
    private String byUser;
}
