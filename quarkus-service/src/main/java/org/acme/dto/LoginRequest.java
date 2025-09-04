package org.acme.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonProperty;

@Setter
@Getter
public class LoginRequest {

    // Getters and Setters
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "Password is required")
    @JsonProperty("password")
    private String password;

    // Default constructor
    public LoginRequest() {
    }

    // Constructor with parameters
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

}

