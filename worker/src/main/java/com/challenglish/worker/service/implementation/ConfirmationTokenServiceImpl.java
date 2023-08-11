package com.challenglish.worker.service.implementation;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.challenglish.worker.entity.ConfirmationToken;
import com.challenglish.worker.repository.ConfirmationTokenRepository;
import com.challenglish.worker.service.ConfirmationTokenService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    public ConfirmationTokenServiceImpl(ConfirmationTokenRepository confirmationTokenRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    @Override
    public void saveConfirmationToken(ConfirmationToken token) {
        log.info(String.format("Service: saving token %s", token.getToken()));
        confirmationTokenRepository.save(token);
    }

    @Override
    public Optional<ConfirmationToken> getToken(String token) {
        log.info(String.format("Service: getting token %s", token));
        return confirmationTokenRepository.findByToken(token);
    }

    @Override
    public void setConfirmedAt(String token) {
        log.info(String.format("Service: confirming token %s", token));
        confirmationTokenRepository.updateConfirmedAt(
                token, LocalDateTime.now());
    }
}
