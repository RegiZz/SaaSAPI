package pl.regizz.saasapi.api.dto;

import jakarta.validation.constraints.NotNull;

public record ChangePlanRequest(
        @NotNull Long newPlanId
) {
}