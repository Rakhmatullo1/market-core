package com.rahmatullo.comfortmarket.service.mapper;

import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.service.dto.*;
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
import java.util.List;
import java.util.Objects;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR, componentModel = "spring")
public abstract class UserMapper {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private PremiseMapper premiseMapper;

    @Autowired
    private WorkerMapper workerMapper;

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

    @Mapping(target = "premiseDtoList", source = "user", qualifiedByName = "getPremises")
    @Mapping(target = "workers", source = "user", qualifiedByName = "getWorkers")
    public abstract UserDtoForOwner toUserDtoForOwner(User user);

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

    @Named(value = "getPremises")
    List<PremiseDto> getPremises(User user) {
        return user.getPremise().stream().map(premiseMapper::toPremiseDto).toList();
    }

    @Named(value = "getWorkers")
    List<WorkerDto> getWorkers(User user) {
        return user.getWorkers().stream().map(workerMapper::toWorkerDto).toList();
    }

    @Named("encodePassword")
    String encodePassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }
}
