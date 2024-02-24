package com.rahmatullo.comfortmarket.service.dto;

import com.rahmatullo.comfortmarket.service.enums.Action;
import lombok.Data;

@Data
public class ProposalRequestDto {
    private String name;
    private String description;
    private Action action;
    private int count;
    private Double sellAmount;
    private Long productId;
}
