package com.example.auth.web.dto;

import com.example.auth.domain.shared.ValidationRules;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RegisterRequest {
    @NotBlank @Size(min = 3, max = 32)
    @Pattern(regexp = ValidationRules.USERNAME_REGEX,
            message = ValidationRules.USERNAME_MESSAGE)
    private String username;

    @NotBlank @Email @Size(max = 120)
    private String email;

    @NotBlank @Size(min = 6, max = 128)
    private String password;
}
