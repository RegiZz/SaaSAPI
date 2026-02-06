package pl.regizz.saasapi.api.dto;

import java.time.Instant;

public record SubscriptionResponse(
        Long id,
        Long userId,
        Long planId,
        String status,
        Instant startDate,
        Instant endDate,
        Instant trialEndDate,
        boolean autoRenew,
        Long pendingPlanId,
        Instant pendingPlanChangeDate
) {
}