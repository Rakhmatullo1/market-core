package com.rahmatullo.comfortmarket.service.mapper;

import com.rahmatullo.comfortmarket.entity.Premise;
import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.service.dto.request.RegisterRequestDto;
import com.rahmatullo.comfortmarket.service.dto.UserDto;
import com.rahmatullo.comfortmarket.service.dto.UserDtoForOwner;
import com.rahmatullo.comfortmarket.service.dto.request.UserRequestDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR, componentModel = "spring")
public abstract class UserMapper {

    @Mapping(target = "income", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "workers", ignore = true)
    @Mapping(target = "premise", ignore = true)
    @Mapping(target = "enabled", constant = "true", resultType = Boolean.class)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", source = "role")
    @Mapping(target = "authorities", ignore = true)
    public abstract User toUser(RegisterRequestDto requestDto);

    @Mapping(target = "owner", source = "user.owner")
    @Mapping(target = "workers", source = "user.workers")
    @Mapping(target = "premise", source = "user.premise")
    @Mapping(target = "enabled", source = "user.enabled")
    @Mapping(target = "password", source = "user.password")
    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "role", source = "user.role")
    @Mapping(target = "fullName", source = "requestDto.fullName")
    @Mapping(target = "username", source = "requestDto.username")
    @Mapping(target = "phoneNumber", source = "requestDto.phoneNumber")
    @Mapping(target = "authorities", ignore = true)
    public abstract User toUser(UserRequestDto requestDto, User user);

    @Mapping(target = "premise", source = "premise", qualifiedByName = "getPremise")
    public abstract UserDto toUserDto(User user);

    @Mapping(target = "premises", ignore = true)
    @Mapping(target = "workers", ignore = true)
    @Mapping(target = "premise", ignore = true)
    public abstract UserDtoForOwner toUserDtoForOwner(User user);

    @Named("getPremise")
    List<String> getPremise(List<Premise> premises) {
        return premises.stream().map(Premise::getName).toList();
    }
}
