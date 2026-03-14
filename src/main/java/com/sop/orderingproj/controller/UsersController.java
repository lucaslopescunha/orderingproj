package com.sop.orderingproj.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sop.orderingproj.dto.CreateUserDto;
import com.sop.orderingproj.service.UserService;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final UserService userService;
    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/new")
    public ResponseEntity<String> createUser(@RequestBody CreateUserDto dto) {
        this.userService.createUser(dto);
        return ResponseEntity.ok("User created.");
    }
}
