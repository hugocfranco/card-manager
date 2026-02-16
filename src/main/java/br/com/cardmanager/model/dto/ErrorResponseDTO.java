package br.com.cardmanager.model.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ErrorResponseDTO(int status,
                               String message,
                               String path,
                               LocalDateTime timestamp) {
}
