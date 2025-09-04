package org.acme.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
public class SignupRequest {

    // Getters and Setters
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @JsonProperty("password")
    private String password;

    // Default constructor (required for JSON deserialization)
    public SignupRequest() {
    }

    // Constructor with parameters
    public SignupRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

}
