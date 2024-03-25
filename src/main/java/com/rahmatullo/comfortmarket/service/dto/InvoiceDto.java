package com.rahmatullo.comfortmarket.service.dto;

import com.rahmatullo.comfortmarket.service.enums.InvoiceStatus;
import lombok.Data;

import java.util.List;

@Data
public class InvoiceDto {
    private Long id;
    private String description;
    private String date;
    private String premise;
    private Double overallInitialPrice;
    private Double overallFinalPrice;
    private List<ProductDetailsDto> products;
    private String toUser;
    private String createdBy;
    private InvoiceStatus status;
    private boolean isApproved;
}
