package com.rahmatullo.comfortmarket.service.dto.request;

import com.rahmatullo.comfortmarket.service.enums.PremiseType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class PremiseRequestDto {
    @NotNull(message = "name cannot be null")
    private String name;
    @NotNull(message = "address cannot be null")
    private String address;
    @NotNull(message = "premiseType cannot be null")
    private PremiseType type;
}
