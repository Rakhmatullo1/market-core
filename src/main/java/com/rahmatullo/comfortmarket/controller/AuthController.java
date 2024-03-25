package com.rahmatullo.comfortmarket.controller;

import com.rahmatullo.comfortmarket.service.AuthService;
import com.rahmatullo.comfortmarket.service.dto.request.RegisterRequestDto;
import com.rahmatullo.comfortmarket.service.dto.ResponseBodyDto;
import com.rahmatullo.comfortmarket.service.dto.request.SignInRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ResponseBodyDto> login(@Valid @RequestBody SignInRequestDto requestDto) {
        return ResponseEntity.ok(authService.signIn(requestDto));
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseBodyDto> register(@Valid @RequestBody RegisterRequestDto requestDto) {
        return ResponseEntity.ok(authService.register(requestDto));
    }
}
