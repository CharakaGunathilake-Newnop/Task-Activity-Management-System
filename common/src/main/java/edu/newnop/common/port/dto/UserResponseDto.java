package edu.newnop.common.port.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private Long id;
    private String name;
    private String email;
    private String userStatus;
    private Instant lastLoginAt;
    private boolean isEnabled;
    private boolean isVerified;
    private Instant createdAt;
    private Instant lastUpdatedAt;
}
