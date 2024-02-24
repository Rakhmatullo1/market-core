package com.rahmatullo.comfortmarket.service.mapper;

import com.rahmatullo.comfortmarket.entity.Premise;
import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.service.dto.RegisterRequestDto;
import com.rahmatullo.comfortmarket.service.dto.UserDto;
import com.rahmatullo.comfortmarket.service.dto.UserDtoForOwner;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR, componentModel = "spring")
public abstract class UserMapper {

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "workers", ignore = true)
    @Mapping(target = "premise", ignore = true)
    @Mapping(target = "enabled", constant = "false", resultType = Boolean.class)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", source = "role")
    public abstract User toUser(RegisterRequestDto requestDto);

    @Mapping(target = "premise", source = "premise", qualifiedByName = "getPremise")
    public abstract UserDto toUserDto(User user);

    @Mapping(target = "premise", ignore = true)
    @Mapping(target = "workers", ignore = true)
    public abstract UserDtoForOwner toUserDtoForOwner(User user);

    @Named("getPremise")
    List<String> getPremise(List<Premise> premises) {
        return premises.stream().map(Premise::getName).toList();
    }
}
