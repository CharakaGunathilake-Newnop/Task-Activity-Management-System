package edu.newnop.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 50, message = "Name must be 1–50 characters")
    @Pattern(
            regexp = "^(?! )(?!.*  )[\\p{L} ]+(?<! )$",
            message = "Name can contain only letters and single spaces, without leading, trailing, or repeated spaces"
    )
    private String name;
    @Email(message = "Email is invalid")
    @NotBlank(message = "Email is required")
    private String email;
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password should contain 8+ uppercase and lowercase characters, letters, numbers, special characters"
    )
    @NotBlank(message = "Password is required")
    private String password;
    @NotBlank(message = "Role is required")
    private String role;
}