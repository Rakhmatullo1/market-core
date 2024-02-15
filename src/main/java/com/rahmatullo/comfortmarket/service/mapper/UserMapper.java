package com.rahmatullo.comfortmarket.service.mapper;

import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.service.dto.RegisterRequestDto;
import com.rahmatullo.comfortmarket.service.dto.UserDto;
import com.rahmatullo.comfortmarket.service.enums.UserRole;
import com.rahmatullo.comfortmarket.service.exception.EmptyFieldException;
import com.rahmatullo.comfortmarket.service.exception.NotFoundException;
import io.micrometer.common.util.StringUtils;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.Objects;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR, componentModel = "spring")
public abstract class UserMapper {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mapping(target = "workers", ignore = true)
    @Mapping(target = "premise", ignore = true)
    @Mapping(target = "enabled", constant = "false", resultType = Boolean.class)
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "username", source = "username" )
    @Mapping(target = "phoneNumber", source = "phoneNumber")
    @Mapping(target = "password", qualifiedByName = "encodePassword", source = "password")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", source = "role", qualifiedByName = "convertRoleToUserRole")
    public abstract User toUser(RegisterRequestDto requestDto);

    @Mapping(target = "role", expression = "java(user.getRole().name())")
    public abstract UserDto toUserDto(User user);

    @Named("convertRoleToUserRole")
    UserRole convertRoleToUserRole(String role) {
        if (Objects.isNull(role) || StringUtils.isEmpty(role)){
            throw new EmptyFieldException("User role is not found");
        }
        return Arrays.stream(UserRole.values())
                .filter(userRole ->Objects.equals(role, userRole.name()))
                .findFirst()
                .orElseThrow(()->new NotFoundException("User role is not found"));
    }

    @Named("encodePassword")
    String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }
}
