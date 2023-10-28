package com.challenglish.worker.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class UserProfileDTO {
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime updateDateTime;
}
