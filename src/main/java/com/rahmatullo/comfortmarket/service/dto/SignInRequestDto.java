package com.rahmatullo.comfortmarket.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignInRequestDto {
    @NotNull(message = "username cannot be null")
    private String username;
    @NotNull(message = "password cannot be null")
    @Size(min = 6)
    private String password;
}
