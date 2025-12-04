package com.astitva.zomatoBackend.ZomatoApp.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}
