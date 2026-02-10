package pl.regizz.saasapi.api.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Map;

public record PlanUpdateRequest(
        @NotNull BigDecimal price,
        @NotNull String billingPeriod,
        @NotNull Map<String, Integer> limits,
        boolean active
) {
}