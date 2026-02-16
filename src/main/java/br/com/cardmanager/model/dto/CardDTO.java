package br.com.cardmanager.model.dto;

import jakarta.validation.constraints.NotBlank;

public record CardDTO(@NotBlank String cardNumber) {
}
