package com.challenglish.worker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.challenglish.worker.entity.User;
import com.challenglish.worker.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationConfig {

    private final static String USER_NOT_FOUND_MSG =
        "Incorrect user ID or password. Try again";
    private final UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            log.info(String.format("Service: loading user with email %s", username));

            User user = userRepository
                .findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_MSG));

            if (user.getIsActive() == null) {
                log.error(USER_NOT_FOUND_MSG);
                throw new UsernameNotFoundException(USER_NOT_FOUND_MSG);
            }

            return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(String.valueOf(user.getRole()))
                .build();
        };
    }
}
