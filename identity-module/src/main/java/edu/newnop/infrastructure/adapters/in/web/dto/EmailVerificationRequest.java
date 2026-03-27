package edu.newnop.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailVerificationRequest {
    @NotBlank(message = "Otp Id is required")
    private String otpId;
    @NotBlank(message = "Otp Code is required")
    private String otpCode;
}
