package com.rahmatullo.comfortmarket.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto{
    private Long id;
    private String username;
    private String fullName;
    private String phoneNumber;
    private boolean enabled;
    private String role;
    private List<String> premise;
}
