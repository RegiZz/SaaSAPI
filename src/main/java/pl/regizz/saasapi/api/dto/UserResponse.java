package pl.regizz.saasapi.api.dto;

import java.time.Instant;

public record UserResponse(
        Long id,
        String email,
        Instant createdAt
) {
}
