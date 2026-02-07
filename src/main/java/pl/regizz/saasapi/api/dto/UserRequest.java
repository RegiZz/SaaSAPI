package pl.regizz.saasapi.api.dto;

import jakarta.validation.constraints.NotBlank;

public record UserRequest(
        @NotBlank String email
) {
}
