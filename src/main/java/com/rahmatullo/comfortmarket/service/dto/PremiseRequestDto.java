package com.rahmatullo.comfortmarket.service.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class PremiseRequestDto {
    private String name;
    private String address;
    private String premiseType;
}
