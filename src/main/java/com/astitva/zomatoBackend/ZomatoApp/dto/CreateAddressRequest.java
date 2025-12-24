package com.astitva.zomatoBackend.ZomatoApp.dto;

import com.astitva.zomatoBackend.ZomatoApp.entities.enums.Country;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateAddressRequest {
    @NotBlank(message = "Address line1 cannot be empty")
    @Size(max = 150, message = "Line1 cannot exceed 150 characters")
    private String line1;

    @Size(max = 150, message = "Line2 cannot exceed 150 characters")
    private String line2;

    @NotBlank(message = "City cannot be empty")
    @Size(max = 80, message = "City cannot exceed 80 characters")
    private String city;

    @NotBlank(message = "State cannot be empty")
    @Size(max = 80, message = "State cannot exceed 80 characters")
    private String state;

    @NotBlank(message = "Postal code cannot be empty")
    @Size(max = 20, message = "Postal code cannot exceed 20 characters")
    private String postalCode;

    private Country country = Country.INDIA; // default

    @JsonProperty("isDefault")
    private boolean isDefault;

}