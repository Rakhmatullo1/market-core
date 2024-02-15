package com.rahmatullo.comfortmarket.service.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDtoForOwner extends UserDto{
    private List<PremiseDto> premiseDtoList;
    private List<UserDto> workers;
}
