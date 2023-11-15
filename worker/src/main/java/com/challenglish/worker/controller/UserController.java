package com.challenglish.worker.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.challenglish.worker.dto.UserDTO;
import com.challenglish.worker.dto.UserProfileDTO;
import com.challenglish.worker.entity.User;
import com.challenglish.worker.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("api/v1/account")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateUser(Principal principal,
        @RequestBody UserProfileDTO newUser) {
        User user = userService.findUserByEmail(principal.getName());
        log.info(String.format("Controller: updating user with id %s", user.getId()));

        return ResponseEntity.status(HttpStatus.OK).body(
            userService.updateUser(user, newUser));
    }
}
