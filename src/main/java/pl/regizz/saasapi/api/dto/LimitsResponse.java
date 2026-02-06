package pl.regizz.saasapi.api.dto;

public record LimitsResponse(
        Integer maxUsers,
        Integer maxProjects
) {
}