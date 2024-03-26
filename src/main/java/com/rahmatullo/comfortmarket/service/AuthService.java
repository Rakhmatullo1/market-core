package com.rahmatullo.comfortmarket.service;

import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.service.dto.request.RegisterRequestDto;
import com.rahmatullo.comfortmarket.service.dto.ResponseBodyDto;
import com.rahmatullo.comfortmarket.service.dto.request.SignInRequestDto;

import java.util.Optional;

public interface AuthService {
    ResponseBodyDto signIn(SignInRequestDto requestDto);

    ResponseBodyDto register(RegisterRequestDto requestDto);

    Optional<User> getUserOptional();

    User getUser();

    User getOwner();
}
