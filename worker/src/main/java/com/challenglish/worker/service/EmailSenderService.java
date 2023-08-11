package com.challenglish.worker.service;

import jakarta.mail.SendFailedException;

public interface EmailSenderService {

    void send(String to, String email) throws SendFailedException;
}
