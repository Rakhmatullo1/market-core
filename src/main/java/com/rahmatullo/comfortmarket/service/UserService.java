package com.rahmatullo.comfortmarket.service;

import com.rahmatullo.comfortmarket.service.dto.UserDto;
import com.rahmatullo.comfortmarket.service.dto.UserDtoForOwner;

import java.util.List;

public interface UserService {
    List<UserDto> findAll();

    UserDto findById(Long id);

    UserDto markUserAsEnabled(Long id, Long premiseId);

    UserDtoForOwner getUserInfo();
}
