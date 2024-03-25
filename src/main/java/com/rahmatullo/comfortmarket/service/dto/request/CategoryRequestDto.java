package com.rahmatullo.comfortmarket.service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryRequestDto {
    @NotNull
    private String name;
}
