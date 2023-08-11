package com.challenglish.worker.service;

import java.util.Optional;

import com.challenglish.worker.entity.ConfirmationToken;

public interface ConfirmationTokenService {
    void saveConfirmationToken(ConfirmationToken token);

    Optional<ConfirmationToken> getToken(String token);

    void setConfirmedAt(String token);
}
