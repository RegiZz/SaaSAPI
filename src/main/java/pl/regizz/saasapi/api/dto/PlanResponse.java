package pl.regizz.saasapi.api.dto;

import java.math.BigDecimal;

public record PlanResponse(
        Long id,
        String code,
        BigDecimal price,
        String billingPeriod,
        Integer maxUsers,
        Integer maxProjects,
        boolean active
) {
}