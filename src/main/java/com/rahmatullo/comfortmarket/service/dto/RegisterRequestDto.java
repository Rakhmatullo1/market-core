package com.rahmatullo.comfortmarket.service.dto;

import lombok.Data;

@Data
public class RegisterRequestDto {
    private String fullName;
    private String username;
    private String phoneNumber;
    private String password;
}
