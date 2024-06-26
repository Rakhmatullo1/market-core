package com.rahmatullo.comfortmarket.controller;

import com.rahmatullo.comfortmarket.service.UserService;
import com.rahmatullo.comfortmarket.service.dto.MessageDto;
import com.rahmatullo.comfortmarket.service.dto.UserDto;
import com.rahmatullo.comfortmarket.service.dto.UserDtoForOwner;
import com.rahmatullo.comfortmarket.service.dto.request.UserRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/{id}")
    public ResponseEntity<UserDto> enableWorker(@PathVariable Long id, @RequestParam Long premiseId) {
        return ResponseEntity.ok(userService.markUserAsEnabled(id, premiseId));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> findAllWorkers(@RequestParam(required = false, defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.findAll(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findWorkerById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/info")
    public ResponseEntity<UserDtoForOwner> getInfo(){
        return ResponseEntity.ok(userService.getUserInfo());
    }

    @PutMapping("/func")
    public ResponseEntity<UserDto> updateProfile(@RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.ok(userService.updateProfile(userRequestDto));
    }

    @DeleteMapping("/func")
    public ResponseEntity<MessageDto> deleteProfile() {
        return ResponseEntity.ok(userService.delete());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageDto> removeUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.removeUser(id));
    }
}
