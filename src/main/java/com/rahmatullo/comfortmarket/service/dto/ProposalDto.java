package com.rahmatullo.comfortmarket.service.dto;

import com.rahmatullo.comfortmarket.service.enums.Action;
import com.rahmatullo.comfortmarket.service.enums.ProposalStatus;
import lombok.Data;

@Data
public class ProposalDto {
    private Long id;
    private String name;
    private String description;
    private String createdAt;
    private String createdBy;
    private Action action;
    private ProposalStatus status;
    private Long count;
    private Double sellAmount;
    private ProductDto productDto;

}
