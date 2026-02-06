package pl.regizz.saasapi.api.dto;

import jakarta.validation.constraints.NotNull;

public record LimitsCheckRequest(
        @NotNull Long userId,
        @NotNull Integer requestedUsers,
        @NotNull Integer requestedProjects
) {
}