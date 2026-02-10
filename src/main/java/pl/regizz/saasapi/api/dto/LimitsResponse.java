package pl.regizz.saasapi.api.dto;

import java.util.Map;

public record LimitsResponse(
        Map<String, Integer> limits
) {
}