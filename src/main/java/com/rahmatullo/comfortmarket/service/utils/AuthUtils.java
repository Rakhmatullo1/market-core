package com.rahmatullo.comfortmarket.service.utils;

import com.rahmatullo.comfortmarket.entity.User;
import com.rahmatullo.comfortmarket.repository.UserRepository;
import com.rahmatullo.comfortmarket.service.enums.UserRole;
import com.rahmatullo.comfortmarket.service.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class AuthUtils {

    private final UserRepository userRepository;

    public AuthUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getUserOptional() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(Objects.isNull(authentication)) {
            throw new NotFoundException("You should sign in");
        }

        String username = authentication.getName();

        return userRepository.findByUsername(username);
    }


    public User getUser() {
        return getUserOptional().orElseThrow(()->{
            log.warn("User name is not found");
            throw new RuntimeException("Username is not found");
        });
    }


    public User getOwner() {
        User user = getUser();

        if(!Objects.equals(user.getRole(), UserRole.OWNER)) {
            user = user.getOwner();
        }

        return user;
    }

}
