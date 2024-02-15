package com.rahmatullo.comfortmarket.service.dto;

import lombok.Data;

@Data
public class PremiseDto {
    private Long id;
    private String premiseType;
    private String address;
    private String name;
    private int workersCount;
}
