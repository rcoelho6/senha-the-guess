package com.senhadeguess.backend.persistence.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PlayerRegistrationRequest(
        @Size(min = 3, max = 36) String playerId,
        @NotBlank @Email @Size(max = 254) String email
) { }
