package com.senhadeguess.backend.gameplay.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record GuessRequest(@NotBlank @Pattern(regexp = "[0-9]{4}") String digits) {
}