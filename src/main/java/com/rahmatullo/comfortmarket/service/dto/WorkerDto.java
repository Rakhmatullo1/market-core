package com.rahmatullo.comfortmarket.service.dto;

import lombok.Data;

@Data
public class WorkerDto {
    private String fullName;
    private String username;
    private String phoneNumber;
    private String password;
    private String role;
    private boolean enabled;
}
