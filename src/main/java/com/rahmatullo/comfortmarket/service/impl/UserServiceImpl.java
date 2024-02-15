package com.rahmatullo.comfortmarket.service.impl;

import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.repository.UserRepository;
import com.rahmatullo.comfortmarket.service.UserService;
import com.rahmatullo.comfortmarket.service.dto.UserDto;
import com.rahmatullo.comfortmarket.service.dto.UserDtoForOwner;
import com.rahmatullo.comfortmarket.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> findAll() {
        log.info("Requested to get all users");
        List<User> users = userRepository.findAll();
        return users.stream().map(userMapper::toUserDto).toList();
    }

    @Override
    public UserDto findById(Long id) {
        log.info("Requested to get user {}", id);
        User user = toUser(id);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto markUserAsEnabled(boolean enabled, Long id) {
        log.info("Requested to change status of user {}", id);

        User user = toUser(id);
        user.setEnabled(enabled);

        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDtoForOwner getUserInfo(Long id) {
        log.info("Requested to get user info");

        User user = toUser(id);

//        List<PremiseDto> premisesInfo = user.getPremise().stream().map().toList();
//        List<WorkerDto> workersInfo = user.getWorkers().stream().map().toList();

//        UserDtoForOwner userDto =

        return null;
    }

    private User toUser(Long id) {
       return userRepository.findById(id).orElseThrow(()->{
            log.warn("User is not found");
            throw new RuntimeException("user is not found "+id);
        });
    }
}
