package com.rahmatullo.comfortmarket.service.impl;

import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.repository.UserRepository;
import com.rahmatullo.comfortmarket.service.AuthService;
import com.rahmatullo.comfortmarket.service.JwtService;
import com.rahmatullo.comfortmarket.service.PremiseService;
import com.rahmatullo.comfortmarket.service.dto.ResponseBodyDto;
import com.rahmatullo.comfortmarket.service.dto.request.PremiseRequestDto;
import com.rahmatullo.comfortmarket.service.dto.request.RegisterRequestDto;
import com.rahmatullo.comfortmarket.service.dto.request.SignInRequestDto;
import com.rahmatullo.comfortmarket.service.enums.PremiseType;
import com.rahmatullo.comfortmarket.service.enums.UserRole;
import com.rahmatullo.comfortmarket.service.exception.DoesNotMatchException;
import com.rahmatullo.comfortmarket.service.exception.ExistsException;
import com.rahmatullo.comfortmarket.service.exception.NotFoundException;
import com.rahmatullo.comfortmarket.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final PremiseService premiseService;
    private final BCryptPasswordEncoder encoder;

    @Override
    public ResponseBodyDto signIn(SignInRequestDto requestDto) {
        log.info("Requested to log in {}", requestDto.getUsername());

        User user = userRepository.findByUsername(requestDto.getUsername()).orElseThrow(()->{
            log.warn("user name is not found {}", requestDto.getUsername() );
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
            throw new ExistsException("username exists, " + username);
        }

        User user = userMapper.toUser(requestDto);
        user.setPassword(encoder.encode(requestDto.getPassword()));

        user = userRepository.save(user);
        String response = jwtService.generateToken(user);

        createBinPremiseForOwner(user);

        log.info("Successfully registered");
        return new ResponseBodyDto(response);
    }

    private void createBinPremiseForOwner(User user) {
        if(Objects.equals(user.getRole(), UserRole.OWNER)){
            premiseService
                    .createPremise(PremiseRequestDto
                            .builder()
                            .name("Bin")
                            .address("virtual")
                            .type(PremiseType.BIN)
                            .build(), user);
        }
    }
}
