package com.rahmatullo.comfortmarket.service.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Getter
@Setter
public class UserDtoForOwner extends UserDto{
    private List<PremiseDto> premiseDtoList;
    private List<WorkerDto> workers;
}
