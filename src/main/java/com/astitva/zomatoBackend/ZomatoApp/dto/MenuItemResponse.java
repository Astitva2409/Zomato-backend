package com.astitva.zomatoBackend.ZomatoApp.dto;

import com.astitva.zomatoBackend.ZomatoApp.entities.enums.MenuCategory;
import lombok.Data;

@Data
public class MenuItemResponse {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private MenuCategory category;
    private boolean available;
}