package com.challenglish.worker.dto;

import java.time.LocalDateTime;

import com.challenglish.worker.entity.Role;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class UserDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private String password;
    private Boolean isActive;
    private LocalDateTime createDateTime;
    private LocalDateTime updateDateTime;
}
