package com.rahmatullo.comfortmarket.service.dto.request;

import com.rahmatullo.comfortmarket.service.enums.Action;
import lombok.Data;

import java.util.List;

@Data
public class InvoiceRequestDto {
    private String description;
    private Long premiseId;
    private Long previousId;
    private Action action;
    private List<ProductDetailsRequestDto> products;
}
