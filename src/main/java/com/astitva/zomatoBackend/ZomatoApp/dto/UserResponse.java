package com.astitva.zomatoBackend.ZomatoApp.dto;

import com.astitva.zomatoBackend.ZomatoApp.entities.enums.UserRole;
import lombok.Data;

import java.util.Set;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Set<UserRole> role;
}
