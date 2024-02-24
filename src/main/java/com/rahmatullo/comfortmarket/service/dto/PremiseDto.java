package com.rahmatullo.comfortmarket.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PremiseDto {
    private Long id;
    private String type;
    private String address;
    private String name;
    private String owner;
    private List<ProductDto> products;
    private List<UserDto> workers;
}
