package com.challenglish.worker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.challenglish.worker.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
    Optional<User> findById(String id);
    @Modifying
    @Query("UPDATE User a " +
        "SET a.isActive = TRUE " +
        "WHERE a.email = ?1 ")
    int activateUser(String email);
}
