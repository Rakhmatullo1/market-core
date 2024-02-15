package com.rahmatullo.comfortmarket.service;

import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.service.dto.RegisterRequestDto;
import com.rahmatullo.comfortmarket.service.dto.ResponseBodyDto;
import com.rahmatullo.comfortmarket.service.dto.SignInRequestDto;

import java.util.Optional;

public interface AuthService {
    ResponseBodyDto signIn(SignInRequestDto requestDto);

    ResponseBodyDto register(RegisterRequestDto requestDto);

    Optional<User> getUserOptional();

    User getUser();
}
