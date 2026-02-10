package pl.regizz.saasapi.api.dto;

import java.math.BigDecimal;
import java.util.Map;

public record PlanResponse(
        Long id,
        String code,
        BigDecimal price,
        String billingPeriod,
        Map<String, Integer> limits,
        boolean active
) {
}