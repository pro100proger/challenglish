package com.challenglish.worker.service.implementation;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.challenglish.worker.dto.UserDTO;
import com.challenglish.worker.dto.UserProfileDTO;
import com.challenglish.worker.mapper.UserMapper;
import com.challenglish.worker.repository.UserRepository;
import com.challenglish.worker.service.UserService;
import com.challenglish.worker.entity.User;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public void enableUser(String email) {
        log.debug(String.format("UserService: enabling user with the email %s", email));
        userRepository.enableUser(email);
    }

    @Override
    public User findUserByEmail(String email) {
        log.info(String.format("UserService: find user with the email %s", email));
        return userRepository.findByEmail(email).
            orElseThrow(EntityNotFoundException::new);
    }

    public UserDTO updateUser(User user, UserProfileDTO newUser) {
        log.info(String.format("UserService: updating user with id %s", user.getId()));

        boolean isPresentButMe = Objects.equals(user.getEmail(), newUser.getEmail());
        if (!isPresentButMe) {
            userRepository.findByEmail(newUser.getEmail()).ifPresent(
                user_ -> {
                    throw new RuntimeException(
                        String.format("Email %s is already taken by another user.", newUser.getEmail()));
                }
            );
        }

        userMapper.updateUser(user, newUser);
        user.setUpdateDateTime(LocalDateTime.now());
        userRepository.save(user);

        return userMapper.entityToDto(user);
    }
}