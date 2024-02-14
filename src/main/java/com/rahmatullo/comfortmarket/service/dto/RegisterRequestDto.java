package com.rahmatullo.comfortmarket.service.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RegisterRequestDto {
    private String fullName;
    private String username;
    private String phoneNumber;
    private String password;
    private String role;
}
