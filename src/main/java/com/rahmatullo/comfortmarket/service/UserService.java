package com.rahmatullo.comfortmarket.service;

import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.service.dto.MessageDto;
import com.rahmatullo.comfortmarket.service.dto.UserDto;
import com.rahmatullo.comfortmarket.service.dto.UserDtoForOwner;
import com.rahmatullo.comfortmarket.service.dto.request.UserRequestDto;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface UserService {
    List<UserDto> findAll(PageRequest pageRequest);

    UserDto findById(Long id);

    UserDtoForOwner getUserInfo();

    UserDto markUserAsEnabled(Long id, Long premiseId);

    UserDto updateProfile(UserRequestDto requestDto);

    MessageDto delete();

    MessageDto removeUser(Long id);

    void checkUser(User userResult);

    User toUser(Long id);
}
