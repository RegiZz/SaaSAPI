package pl.regizz.saasapi.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Map;

public record PlanRequest(
        @NotBlank String code,
        @NotNull BigDecimal price,
        @NotNull String billingPeriod,
        @NotNull Map<String, Integer> limits
) {
}