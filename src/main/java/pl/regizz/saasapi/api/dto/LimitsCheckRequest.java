package pl.regizz.saasapi.api.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record LimitsCheckRequest(
        @NotNull Long userId,
        @NotNull Map<String, Integer> requestedLimits
) {
}