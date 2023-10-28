package com.challenglish.worker.service;

import java.security.Principal;

import org.springframework.web.bind.annotation.RequestBody;

import com.challenglish.worker.dto.UserDTO;
import com.challenglish.worker.dto.UserProfileDTO;
import com.challenglish.worker.entity.User;

public interface UserService {

    void enableUser(String email);

    User findUserByEmail(String email);

    UserDTO updateUser(User user, UserProfileDTO newUser);
}