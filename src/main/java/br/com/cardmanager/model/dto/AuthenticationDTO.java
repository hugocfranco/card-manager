package br.com.cardmanager.model.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationDTO(@NotBlank String login,
                                @NotBlank String password) {
}