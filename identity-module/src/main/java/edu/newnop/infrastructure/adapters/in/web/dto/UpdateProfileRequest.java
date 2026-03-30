package edu.newnop.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    @Email
    private String email;
    @Size(min = 1, max = 50, message = "Name must be 1–50 characters")
    @Pattern(
            regexp = "^(?! )(?!.*  )[\\p{L} ]+(?<! )$",
            message = "Name can contain only letters and single spaces, without leading, trailing, or repeated spaces"
    )
    private String name;
}
