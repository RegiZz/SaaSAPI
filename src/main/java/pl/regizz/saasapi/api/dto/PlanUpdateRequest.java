package pl.regizz.saasapi.api.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PlanUpdateRequest(
        @NotNull BigDecimal price,
        @NotNull String billingPeriod,
        @NotNull Integer maxUsers,
        @NotNull Integer maxProjects,
        boolean active
) {
}