package com.rahmatullo.comfortmarket.service;

import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.service.dto.UserDto;
import com.rahmatullo.comfortmarket.service.dto.UserDtoForOwner;
import com.rahmatullo.comfortmarket.service.dto.UserRequestDto;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface UserService {
    List<UserDto> findAll(PageRequest pageRequest);

    UserDto findById(Long id);

    UserDtoForOwner getUserInfo();

    UserDto markUserAsEnabled(Long id, Long premiseId);

    //TODO finishing update method
    UserDto updateProfile(UserRequestDto requestDto);

    void checkUser(User userResult);

    User toUser(Long id);
}
