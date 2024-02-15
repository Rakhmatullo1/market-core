package com.rahmatullo.comfortmarket.service.impl;

import com.rahmatullo.comfortmarket.entity.Premise;
import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.entity.Worker;
import com.rahmatullo.comfortmarket.repository.PremiseRepository;
import com.rahmatullo.comfortmarket.repository.UserRepository;
import com.rahmatullo.comfortmarket.repository.WorkerRepository;
import com.rahmatullo.comfortmarket.service.AuthService;
import com.rahmatullo.comfortmarket.service.UserService;
import com.rahmatullo.comfortmarket.service.dto.UserDto;
import com.rahmatullo.comfortmarket.service.dto.UserDtoForOwner;
import com.rahmatullo.comfortmarket.service.exception.DoesNotMatchException;
import com.rahmatullo.comfortmarket.service.exception.NotFoundException;
import com.rahmatullo.comfortmarket.service.mapper.UserMapper;
import com.rahmatullo.comfortmarket.service.mapper.WorkerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final WorkerMapper workerMapper;
    private final AuthService authService;
    private final WorkerRepository workerRepository;
    private final PremiseRepository premiseRepository;


    @Override
    public List<UserDto> findAll() {
        log.info("Requested to get all users");
        List<User> users = userRepository.findAll();
        return users.stream().map(userMapper::toUserDto).toList();
    }

    @Override
    public UserDto findById(Long id) {
        log.info("Requested to get user {}", id);

        checkNull(id, "User id is not given");

        User user = toUser(id);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto markUserAsEnabled(Long id, Long premiseId) {
        log.info("Requested to change status of user {}", id);

        User ownerUser = authService.getUser();

        checkNull(id, "User id is not given");
        checkNull(premiseId, "Premise id is not given");

        User user = toUser(id);
        user.setEnabled(true);

        Premise premise = premiseRepository.findById(id).orElseThrow(()->{
            log.warn("Premise is not found");
            throw new NotFoundException("Premise is not found");
        });

        if(ownerUser.getPremise().stream().anyMatch(premise1 -> Objects.equals(premise1,premise))){
            log.warn("Does not premise id with yours");
            throw new DoesNotMatchException(String.format("Premise id is not matched with owner user %s's premises", user.getUsername()));
        };

        Worker savedWorker = workerRepository.save(workerMapper.toWorker(user, premise));

        List<Worker> workers = ownerUser.getWorkers();
        workers.add(savedWorker);
        ownerUser.setWorkers(workers);

        userRepository.save(ownerUser);
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDtoForOwner getUserInfo() {
        log.info("Requested to get user info");

        User user = authService.getUser();
        UserDtoForOwner userDtoForOwner = userMapper.toUserDtoForOwner(user);

        log.info("Successfully fetched user info {}", user.getUsername());
        return userDtoForOwner;
    }

    private User toUser(Long id) {
       return userRepository.findById(id).orElseThrow(()->{
            log.warn("User is not found");
            throw new RuntimeException("user is not found "+id);
        });
    }

    public static void checkNull(Object object, String message) {
        if(Objects.isNull(object)) {
            throw new NotFoundException(message);
        }
    }
}
