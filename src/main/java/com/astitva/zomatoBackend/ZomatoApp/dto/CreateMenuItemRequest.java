package com.astitva.zomatoBackend.ZomatoApp.dto;

import com.astitva.zomatoBackend.ZomatoApp.entities.enums.MenuCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateMenuItemRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private Double price;

    @NotNull
    private MenuCategory category;

    private boolean available = true;
}