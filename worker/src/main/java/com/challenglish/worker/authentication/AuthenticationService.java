package com.challenglish.worker.authentication;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.challenglish.worker.config.JwtService;
import com.challenglish.worker.entity.ConfirmationToken;
import com.challenglish.worker.entity.Role;
import com.challenglish.worker.entity.User;
import com.challenglish.worker.repository.UserRepository;
import com.challenglish.worker.service.ConfirmationTokenService;
import com.challenglish.worker.service.EmailSenderService;
import com.challenglish.worker.service.UserService;

import jakarta.mail.SendFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final static String TOKEN_ALREADY_CONFIRMED = "Service: token %s is already confirmed";
    private final static String TOKEN_EXPIRED = "Service: token %s expired";
    private final static String LOGIN_ROUTE = "<meta http-equiv=\"refresh\" content=\"0;" +
        " url=http://localhost:3000/login\" />";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ConfirmationTokenService confirmationTokenService;
    private final UserService userService;
    private final EmailSenderService emailSender;

    public AuthenticationResponse register(RegisterRequest request) throws IOException, SendFailedException {

        log.info(String.format("Service: registering user with email %s", request.getEmail()));

        boolean userExists = userRepository
            .findByEmail(request.getEmail())
            .isPresent();

        if (userExists) {
            log.error(String.format("Service: email %s already taken", request.getEmail()));
            throw new RuntimeException();
        }

        User user = User.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.USER)
            .createDateTime(LocalDateTime.now())
            .updateDateTime(LocalDateTime.now())
            .build();
        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);

        ConfirmationToken confirmationToken = new ConfirmationToken(
            jwtToken,
            LocalDateTime.now(),
            LocalDateTime.now().plusMinutes(15),
            user
        );

        confirmationTokenService.saveConfirmationToken(confirmationToken);

        String link = "http://localhost:8765/worker/api/v1/registration/confirm?token=" + jwtToken;

        emailSender.send(
            request.getEmail(),
            buildEmail(link));

        return AuthenticationResponse
            .builder()
            .token(jwtToken)
            .build();
    }

    @Transactional
    public String confirmToken(String token) {
        log.info(String.format("Service: confirming token %s", token));
        ConfirmationToken confirmationToken = confirmationTokenService
            .getToken(token)
            .orElseThrow(() -> new RuntimeException("Service: Error with token " + token));

        if (confirmationToken.getConfirmedAt() != null) {
            log.error(String.format(TOKEN_ALREADY_CONFIRMED, token));
            throw new RuntimeException();
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            log.error(String.format(TOKEN_EXPIRED, token));
            throw new RuntimeException();
        }

        confirmationTokenService.setConfirmedAt(token);
        userService.activateUser(
            confirmationToken.getUser().getEmail());

        return LOGIN_ROUTE;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow();

        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse
            .builder()
            .token(jwtToken)
            .build();
    }

    private String buildEmail(String link) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.US);
        String date = "\n" + LocalDateTime.now().format(formatter);

        StringBuilder email = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader("worker/src/main/resources/templates/emailConfirmationLetter.html"))) {
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
