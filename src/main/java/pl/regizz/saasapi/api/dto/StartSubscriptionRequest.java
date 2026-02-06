package pl.regizz.saasapi.api.dto;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record StartSubscriptionRequest(
        @NotNull Long userId,
        @NotNull Long planId,
        @NotNull Instant trialEndDate,
        boolean autoRenew
) {
}