package com.rahmatullo.comfortmarket.service.impl;

import com.rahmatullo.comfortmarket.entity.Premise;
import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.repository.PremiseRepository;
import com.rahmatullo.comfortmarket.repository.UserRepository;
import com.rahmatullo.comfortmarket.service.UserService;
import com.rahmatullo.comfortmarket.service.dto.MessageDto;
import com.rahmatullo.comfortmarket.service.dto.PremiseDto;
import com.rahmatullo.comfortmarket.service.dto.UserDto;
import com.rahmatullo.comfortmarket.service.dto.UserDtoForOwner;
import com.rahmatullo.comfortmarket.service.dto.request.UserRequestDto;
import com.rahmatullo.comfortmarket.service.enums.UserRole;
import com.rahmatullo.comfortmarket.service.exception.DoesNotMatchException;
import com.rahmatullo.comfortmarket.service.exception.ExistsException;
import com.rahmatullo.comfortmarket.service.exception.NotFoundException;
import com.rahmatullo.comfortmarket.service.mapper.PremiseMapper;
import com.rahmatullo.comfortmarket.service.mapper.UserMapper;
import com.rahmatullo.comfortmarket.service.utils.AuthUtils;
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
    private final AuthUtils authUtils;
    private final PremiseRepository premiseRepository;
    private final PremiseMapper premiseMapper;

    public static  <T> List<T> getList(List<T> list) {
        if(list.size()>3) {
            return list.subList(0,3);
        }
        return list;
    }

    @Override
    public List<UserDto> findAll(PageRequest pageRequest) {
        log.info("Requested to get all users");
        User user = authUtils.getUser();

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

        User user = authUtils.getUser();
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

        User owner = authUtils.getUser();
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
        log.info("Requested to update profile");
        User user = authUtils.getUser();

        if(!Objects.equals(user.getUsername(), requestDto.getUsername()) && userRepository.existsByUsername(requestDto.getUsername())) {
           log.warn("The username exists ");
           throw new ExistsException("Username exists " + requestDto.getUsername());
        }

        user = userMapper.toUser(requestDto, user);

        log.info("Successfully updated");
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public MessageDto delete() {
        log.info("Requested to delete user");
        User user = authUtils.getUser();
        deleteUser(user);
        userRepository.delete(userRepository.save(user));

        log.info("Successfully deleted ");
        return new MessageDto("Successfully deleted");
    }

    @Override
    public MessageDto removeUser(Long id) {
        log.info("Requested to remove User ");
        User user = toUser(id);
        checkUser(user);

        deleteUser(user);
        userRepository.save(user);

        log.info("Successfully deleted");
        return new MessageDto("Successfully removed");
    }

    @Override
    public void checkUser(User userResult) {
        User user = authUtils.getUser();

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

    private void deleteUser(User user) {
        user.getPremise().forEach(premise -> premise.getWorkers().remove(user));

        user.setPremise(null);
        user.setOwner(null);
        user.setEnabled(false);
    }
}
