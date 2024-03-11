package com.rahmatullo.comfortmarket.service.dto;

import lombok.Data;

@Data
public class ProductTransferDto {
    private Long previousId;
    private Long destinationId;
    private Long count;
}
