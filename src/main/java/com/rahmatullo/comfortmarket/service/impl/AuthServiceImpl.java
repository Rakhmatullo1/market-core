package com.rahmatullo.comfortmarket.service.impl;

import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.repository.UserRepository;
import com.rahmatullo.comfortmarket.service.AuthService;
import com.rahmatullo.comfortmarket.service.JwtService;
import com.rahmatullo.comfortmarket.service.dto.RegisterRequestDto;
import com.rahmatullo.comfortmarket.service.dto.ResponseBodyDto;
import com.rahmatullo.comfortmarket.service.dto.SignInRequestDto;
import com.rahmatullo.comfortmarket.service.enums.UserRole;
import com.rahmatullo.comfortmarket.service.exception.DoesNotMatchException;
import com.rahmatullo.comfortmarket.service.exception.ExistsException;
import com.rahmatullo.comfortmarket.service.exception.NotFoundException;
import com.rahmatullo.comfortmarket.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder encoder;

    @Override
    public ResponseBodyDto signIn(SignInRequestDto requestDto) {
        log.info("Requested to log in {}", requestDto.getUsername());

        User user = userRepository.findByUsername(requestDto.getUsername()).orElseThrow(()->{
            log.warn("user name is not found {}",requestDto.getUsername() );
            throw new NotFoundException("User name is not found");
        });

        String password  = user.getPassword();

        if(!encoder.matches(requestDto.getPassword(), password)){
            log.warn("password does not match {}", requestDto.getPassword());
            throw new DoesNotMatchException("password does not match");
        }

        log.info("Successfully logged in");
        return new ResponseBodyDto(jwtService.generateToken(user));
    }

    @Override
    public ResponseBodyDto register(RegisterRequestDto requestDto) {
        log.info("Requested to register new user [{}]", requestDto);
        String username = requestDto.getUsername();

        if(userRepository.existsByUsername(username)) {
            log.warn("Username exists in db {}", username);
            throw new ExistsException("username exists, " +username);
        }

        User user = userMapper.toUser(requestDto);

        if(Objects.equals(user.getRole(), UserRole.OWNER)) {
            user.setEnabled(true);
        }

        UserDetails userDetails  = userRepository.save(user);

        log.info("Successfully registered");
        return new ResponseBodyDto(jwtService.generateToken(userDetails));
    }

    @Override
    public Optional<User> getUserOptional() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(Objects.isNull(authentication)) {
            throw new NotFoundException("You should sign in");
        }

        String username = authentication.getName();

        return userRepository.findByUsername(username);
    }

    @Override
    public User getUser() {
        return getUserOptional().orElseThrow(()->{
            log.warn("User name is not found");
            throw new RuntimeException("Username is not found");
        });
    }
}
