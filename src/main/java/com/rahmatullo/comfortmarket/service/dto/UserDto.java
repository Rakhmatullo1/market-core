package com.rahmatullo.comfortmarket.service.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class UserDto {
    private Long id;
    private String username;
    private String fullName;
    private String phoneNumber;
    private boolean enabled;
    private String role;
}
