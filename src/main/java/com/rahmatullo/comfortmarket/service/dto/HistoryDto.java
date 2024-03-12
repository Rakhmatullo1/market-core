package com.rahmatullo.comfortmarket.service.dto;

import com.rahmatullo.comfortmarket.service.enums.Action4Product;
import lombok.Data;

@Data
public class HistoryDto {
    private Long id;
    private String productName;
    private String description;
    private String createdAt;
    private String byUser;
    private Action4Product action;
}
