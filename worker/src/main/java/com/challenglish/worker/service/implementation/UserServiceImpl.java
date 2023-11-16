package com.challenglish.worker.service.implementation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.challenglish.worker.config.JwtService;
import com.challenglish.worker.dto.UserDTO;
import com.challenglish.worker.dto.UserProfileDTO;
import com.challenglish.worker.entity.ConfirmationToken;
import com.challenglish.worker.mapper.UserMapper;
import com.challenglish.worker.repository.ConfirmationTokenRepository;
import com.challenglish.worker.repository.UserRepository;
import com.challenglish.worker.service.ConfirmationTokenService;
import com.challenglish.worker.service.EmailSenderService;
import com.challenglish.worker.service.UserService;
import com.challenglish.worker.entity.User;

import jakarta.mail.SendFailedException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSenderService emailSender;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ConfirmationTokenRepository confirmationTokenRepository, UserMapper userMapper, JwtService jwtService,
        ConfirmationTokenService confirmationTokenService, EmailSenderService emailSender) {
        this.userRepository = userRepository;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
        this.confirmationTokenService = confirmationTokenService;
        this.emailSender = emailSender;
    }

    @Override
    public void activateUser(String email) {
        log.debug(String.format("UserService: activating user with the email %s", email));
        userRepository.activateUser(email);
    }

    @Override
    public User findUserByEmail(String email) {
        log.info(String.format("UserService: find user with the email %s", email));
        return userRepository.findByEmail(email).
            orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public UserDTO updateUser(User user, UserProfileDTO newUser) throws IOException, SendFailedException {
        log.info(String.format("UserService: updating user with id %s", user.getId()));

        boolean isPresentButMe = Objects.equals(user.getEmail(), newUser.getEmail());
        if (!isPresentButMe) {
            if (userRepository.findByEmail(newUser.getEmail()).isPresent()) {
                throw new RuntimeException(
                    String.format("Email %s is already taken by another user.", newUser.getEmail()));
            }
            else {
                user.setIsActive(false);

                String jwt = jwtService.generateToken(user);
                if (confirmationTokenRepository.findByUserId(user.getId()).isPresent()) {
                    ConfirmationToken confirmationToken = confirmationTokenRepository.findByUserId(user.getId()).get();
                    confirmationToken.setToken(jwt);
                    confirmationToken.setCreatedAt(LocalDateTime.now());
                    confirmationToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
                    confirmationToken.setConfirmedAt(null);
                    confirmationToken.setUser(user);

                    confirmationTokenService.saveConfirmationToken(confirmationToken);
                } else {
                    throw new RuntimeException(
                        String.format("Confirmation token with userId %s is not exist. ", user.getId()));
                }

                String link = "http://localhost:8765/worker/api/v1/registration/confirm?token=" + jwt;

                emailSender.send(
                    newUser.getEmail(),
                    buildEmail(link));
            }
        }
        userMapper.updateUser(user, newUser);
        user.setUpdateDateTime(LocalDateTime.now());
        userRepository.save(user);

        return userMapper.entityToDto(user);
    }

    private String buildEmail(String link) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.US);
        String date = "\n" + LocalDateTime.now().format(formatter);

        StringBuilder email = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader("worker/src/main/resources/templates/confirmationLetterChangeEmail.html"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                email.append(line).append(System.lineSeparator());
            }
        }

        email
            .insert(email.indexOf("Project") + 3, date)
            .insert(email.indexOf("href=\"\"") + 6, link);

        return email.toString();
    }
}