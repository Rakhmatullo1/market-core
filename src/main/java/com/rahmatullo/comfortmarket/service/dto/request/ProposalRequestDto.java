package com.rahmatullo.comfortmarket.service.dto.request;

import com.rahmatullo.comfortmarket.service.enums.Action;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProposalRequestDto {
    @NotNull(message = "name cannot be null")
    private String name;
    @NotNull(message = "description cannot be null")
    private String description;
    @NotNull(message = "action cannot be null")
    private Action action;
    @NotNull(message = "count cannot be null")
    private Long count;
    @NotNull(message = "sellAmount cannot be null")
    private Double sellAmount;
    @NotNull(message = "productId cannot be null")
    private Long productId;
}
