package com.challenglish.worker.service;

import com.challenglish.worker.entity.User;

public interface UserService {

    void enableUser(String email);

    User findUserByEmail(String email);

}