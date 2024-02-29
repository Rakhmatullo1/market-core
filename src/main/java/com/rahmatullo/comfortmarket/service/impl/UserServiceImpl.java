package com.rahmatullo.comfortmarket.service.impl;

import com.rahmatullo.comfortmarket.entity.Premise;
import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.repository.PremiseRepository;
import com.rahmatullo.comfortmarket.repository.UserRepository;
import com.rahmatullo.comfortmarket.service.AuthService;
import com.rahmatullo.comfortmarket.service.UserService;
import com.rahmatullo.comfortmarket.service.dto.*;
import com.rahmatullo.comfortmarket.service.enums.UserRole;
import com.rahmatullo.comfortmarket.service.exception.DoesNotMatchException;
import com.rahmatullo.comfortmarket.service.exception.NotFoundException;
import com.rahmatullo.comfortmarket.service.mapper.PremiseMapper;
import com.rahmatullo.comfortmarket.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthService authService;
    private final PremiseRepository premiseRepository;
    private final PremiseMapper premiseMapper;

    @Override
    public List<UserDto> findAll(PageRequest pageRequest) {
        log.info("Requested to get all users");
        User user = authService.getUser();

        List<User> users = findUsers(user.getRole(), pageRequest);
        return users.stream().map(u->{
            UserDto userDto = userMapper.toUserDto(u);
                    userDto.setPremise(null);
                    return userDto;
        }).toList();
    }

    @Override
    public UserDto findById(Long id) {
        log.info("Requested to get user {}", id);
        User userResult = toUser(id);

        checkUser(userResult);

        return userMapper.toUserDto(userResult);
    }

    @Override
    public UserDtoForOwner getUserInfo() {
        log.info("Requested to get user info");

        User user = authService.getUser();
        UserDtoForOwner userDtoForOwner = userMapper.toUserDtoForOwner(user);

        List<UserDto> users =user.getWorkers().stream().map(userMapper::toUserDto).toList();

        userDtoForOwner.setWorkers(getList(users));
        userDtoForOwner.setPremises(getList(getPremises4Owner(user)));

        log.info("Successfully fetched user info {}", user.getUsername());
        return userDtoForOwner;
    }

    @Override
    public UserDto markUserAsEnabled(Long id, Long premiseId) {
        log.info("Requested to change status of user {}", id);

        User owner = authService.getUser();
        User user = toUser(id);

        if(Objects.equals(owner.getRole(), UserRole.ADMIN)){
            log.info("Successfully enabled by {}", owner.getRole());
            return userMapper.toUserDto(enableUser(user));
        }

        checkUser(user);
        checkPremise(premiseId, owner);

        Premise premise = premiseRepository
                .findByOwnerAndId(owner, premiseId)
                .orElseThrow(()->new NotFoundException("Premise is not found"));

        user.getPremise().add(premise);
        user.setOwner(owner);

        return userMapper.toUserDto(enableUser(user));
    }

    @Override
    public UserDto updateProfile(UserRequestDto requestDto) {
        User user = authService.getUser();
        return null;
    }

    @Override
    public void checkUser(User userResult) {
        User user = authService.getUser();

        List<User> users = findUsers(user.getRole(), Pageable.unpaged());

        if( !users.contains(userResult)){
            log.warn("the User is not found or null");
            throw new NotFoundException("User which searched is not found");
        }
    }

    @Override
    public User toUser(Long id) {
        return userRepository.findById(id).orElseThrow(()->new NotFoundException("User is not found"));
    }

    private List<User> findUsers(UserRole role, Pageable pageRequest) {
        if(Objects.equals(role, UserRole.ADMIN)) {
            return  userRepository.findByRoleIn(List.of(UserRole.WORKER.name(), UserRole.OWNER.name()), pageRequest).getContent();
        }
        return userRepository.findByRoleIn(List.of(UserRole.WORKER.name()), pageRequest).getContent();
    }

    private <T> List<T> getList(List<T> list) {
        if(list.size()>3) {
            return list.subList(0,3);
        }
        return list;
    }

    private List<PremiseDto> getPremises4Owner(User user) {
        return  user.getPremise().stream().map(premise -> {
            PremiseDto premiseDto = premiseMapper.toPremiseDto(premise);
            premise.setProducts(getList(premise.getProducts()));
            premiseDto.setWorkers(null);
            premiseDto.setProducts(null);
            return premiseDto;
        }).toList();
    }

    private User enableUser(User user) {
        user.setEnabled(true);
        return  userRepository.save(user);
    }

    private void checkPremise(Long id, User owner) {
        if(!premiseRepository.existsByOwnerAndId(owner, id)){
            log.warn("Premise is not belonged to you");
            throw new DoesNotMatchException("Premise is not belonged to you");
        }
    }
}
