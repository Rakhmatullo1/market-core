package com.rahmatullo.comfortmarket.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDto {
    private Long id;
    private String name;
    private String barcode;
    private String createdAt;
    private int count;
    private Double price;
    private String url;
    private String category;
    private String addedBy;
}
