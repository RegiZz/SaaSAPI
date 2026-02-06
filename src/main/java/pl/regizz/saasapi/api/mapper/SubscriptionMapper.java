package pl.regizz.saasapi.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.regizz.saasapi.api.dto.SubscriptionResponse;
import pl.regizz.saasapi.domain.model.Subscription;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    @Mapping(target = "userId", expression = "java(subscription.getUser().getId())")
    @Mapping(target = "planId", expression = "java(subscription.getPlan().getId())")
    @Mapping(target = "status", expression = "java(subscription.getStatus().name())")
    @Mapping(target = "pendingPlanId", expression = "java(subscription.getPendingPlan() == null ? null : subscription.getPendingPlan().getId())")
    SubscriptionResponse toResponse(Subscription subscription);
}