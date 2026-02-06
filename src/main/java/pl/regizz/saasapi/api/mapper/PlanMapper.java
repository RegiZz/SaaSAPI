package pl.regizz.saasapi.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.regizz.saasapi.api.dto.PlanResponse;
import pl.regizz.saasapi.domain.model.Plan;

@Mapper(componentModel = "spring")
public interface PlanMapper {

    @Mapping(target = "billingPeriod", expression = "java(plan.getBillingPeriod().name())")
    PlanResponse toResponse(Plan plan);
}