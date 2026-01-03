package com.astitva.zomatoBackend.ZomatoApp.dto;

import com.astitva.zomatoBackend.ZomatoApp.entities.enums.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRoleRequest {

    @NotNull(message = "Role is required")
    private UserRole role;
}
