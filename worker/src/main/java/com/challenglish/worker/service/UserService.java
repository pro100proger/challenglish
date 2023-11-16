package com.challenglish.worker.service;

import java.io.IOException;
import java.security.Principal;

import org.springframework.web.bind.annotation.RequestBody;

import com.challenglish.worker.dto.UserDTO;
import com.challenglish.worker.dto.UserProfileDTO;
import com.challenglish.worker.entity.User;

import jakarta.mail.SendFailedException;

public interface UserService {

    void activateUser(String email);

    User findUserByEmail(String email);

    UserDTO updateUser(User user, UserProfileDTO newUser) throws IOException, SendFailedException;
}