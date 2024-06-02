package com.rahmatullo.comfortmarket.service.dto.request;

import com.rahmatullo.comfortmarket.service.enums.UserRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    @NotNull(message = "role cannot be null")
    private UserRole role;
}
