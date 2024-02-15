package com.rahmatullo.comfortmarket.controller;

import com.rahmatullo.comfortmarket.service.UserService;
import com.rahmatullo.comfortmarket.service.dto.UserDto;
import com.rahmatullo.comfortmarket.service.dto.UserDtoForOwner;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDto>> findAllWorkers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findWorkerById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping("/{id}")
    public ResponseEntity<UserDto> enableWorker(@PathVariable Long id, @RequestParam Long premiseId) {
        return ResponseEntity.ok(userService.markUserAsEnabled(id, premiseId));
    }

    @GetMapping("/info")
    public ResponseEntity<UserDtoForOwner> getInfo(){
        return ResponseEntity.ok(userService.getUserInfo());
    }
}
