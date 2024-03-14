package com.rahmatullo.comfortmarket.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.util.List;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class ProductDto {
    private Long id;
    private String name;
    private String barcode;
    private String createdAt;
    private List<ProductCountDto> extra;
    private Double price;
    private String url;
    private String category;
    private String addedBy;
}
