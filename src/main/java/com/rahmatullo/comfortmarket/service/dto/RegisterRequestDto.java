package com.rahmatullo.comfortmarket.service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RegisterRequestDto {
    @NotNull(message = "full name cannot be null")
    private String fullName;
    @NotNull(message = "username cannot be null")
    private String username;
    @NotNull(message = "phoneNumber cannot be null")
    private String phoneNumber;
    @NotNull(message = "password cannot be null")
    private String password;
    @NotNull(message = "role cannot be null")
    private String role;
}
