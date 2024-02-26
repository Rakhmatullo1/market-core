package com.rahmatullo.comfortmarket.service;

import com.rahmatullo.comfortmarket.entity.Premise;
import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.service.dto.UserDto;
import com.rahmatullo.comfortmarket.service.dto.UserDtoForOwner;
import com.rahmatullo.comfortmarket.service.dto.UserRequestDto;

import java.util.List;

public interface UserService {
    List<UserDto> findAll();

    UserDto findById(Long id);

    UserDto markUserAsEnabled(Long id, Long premiseId);

    UserDtoForOwner getUserInfo();

    UserDto updateProfile(UserRequestDto requestDto);

    void checkUser(User userResult);

    void addUsers2Premise(User user, Premise premise);

    User toUser(Long id);
}
