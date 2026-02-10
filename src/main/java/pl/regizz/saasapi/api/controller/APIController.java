package pl.regizz.saasapi.api.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.regizz.saasapi.api.dto.*;
import pl.regizz.saasapi.api.mapper.PlanMapper;
import pl.regizz.saasapi.api.mapper.SubscriptionMapper;
import pl.regizz.saasapi.domain.model.BillingPeriod;
import pl.regizz.saasapi.domain.model.Plan;
import pl.regizz.saasapi.domain.model.Subscription;
import pl.regizz.saasapi.domain.model.User;
import pl.regizz.saasapi.domain.service.SubscriptionService;
import pl.regizz.saasapi.infrastructure.persistence.PlanRepository;
import pl.regizz.saasapi.infrastructure.persistence.UserRepository;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class APIController {

    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final SubscriptionService subscriptionService;
    private final PlanMapper planMapper;
    private final SubscriptionMapper subscriptionMapper;

    public APIController(PlanRepository planRepository,
                         UserRepository userRepository,
                         SubscriptionService subscriptionService,
                         PlanMapper planMapper,
                         SubscriptionMapper subscriptionMapper) {
        this.planRepository = planRepository;
        this.userRepository = userRepository;
        this.subscriptionService = subscriptionService;
        this.planMapper = planMapper;
        this.subscriptionMapper = subscriptionMapper;
    }

    @PostMapping("/plans")
    public ResponseEntity<PlanResponse> createPlan(@Valid @RequestBody PlanRequest request) {
        Plan plan = planRepository.findByCode(request.code())
                .orElseGet(() -> new Plan(
                        request.code(),
                        request.price(),
                        BillingPeriod.valueOf(request.billingPeriod()),
                        request.limits()
                ));
        if (plan.getId() != null) {
            plan.updatePlan(
                    request.price(),
                    BillingPeriod.valueOf(request.billingPeriod()),
                    request.limits(),
                    plan.isActive()
            );
        }
        Plan saved = planRepository.save(plan);
        return ResponseEntity.ok(planMapper.toResponse(saved));
    }

    @GetMapping("/plans")
    public ResponseEntity<List<PlanResponse>> getPlans() {
        List<PlanResponse> plans = planRepository.findAll().stream()
                .map(planMapper::toResponse)
                .toList();
        return ResponseEntity.ok(plans);
    }

    @PutMapping("/plans/{planId}")
    public ResponseEntity<PlanResponse> updatePlan(@PathVariable Long planId,
                                                   @Valid @RequestBody PlanUpdateRequest request) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new pl.regizz.saasapi.exception.NotFoundException("Plan not found"));
        plan.updatePlan(
                request.price(),
                BillingPeriod.valueOf(request.billingPeriod()),
                request.limits(),
                request.active()
        );
        Plan saved = planRepository.save(plan);
        return ResponseEntity.ok(planMapper.toResponse(saved));
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseGet(() -> userRepository.save(new User(request.email())));
        return ResponseEntity.ok(new UserResponse(user.getId(), user.getEmail(), user.getCreatedAt()));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new pl.regizz.saasapi.exception.NotFoundException("User not found"));
        return ResponseEntity.ok(new UserResponse(user.getId(), user.getEmail(), user.getCreatedAt()));
    }

    @PostMapping("/subscriptions/start-trial")
    public ResponseEntity<SubscriptionResponse> startTrial(@Valid @RequestBody StartSubscriptionRequest request) {
        Subscription subscription = subscriptionService.startTrial(
                request.userId(),
                request.planId(),
                request.trialEndDate(),
                request.autoRenew()
        );
        return ResponseEntity.ok(subscriptionMapper.toResponse(subscription));
    }

    @PostMapping("/subscriptions/{subscriptionId}/change-plan")
    public ResponseEntity<SubscriptionResponse> changePlan(@PathVariable Long subscriptionId,
                                                           @Valid @RequestBody ChangePlanRequest request) {
        Subscription subscription = subscriptionService.changePlan(subscriptionId, request.newPlanId());
        return ResponseEntity.ok(subscriptionMapper.toResponse(subscription));
    }

    @PostMapping("/subscriptions/{subscriptionId}/cancel")
    public ResponseEntity<SubscriptionResponse> cancelSubscription(@PathVariable Long subscriptionId) {
        Subscription subscription = subscriptionService.cancelSubscription(subscriptionId);
        return ResponseEntity.ok(subscriptionMapper.toResponse(subscription));
    }

    @GetMapping("/subscriptions/current/{userId}")
    public ResponseEntity<SubscriptionResponse> getCurrentSubscription(@PathVariable Long userId) {
        Subscription subscription = subscriptionService.getCurrentSubscription(userId);
        return ResponseEntity.ok(subscriptionMapper.toResponse(subscription));
    }

    @GetMapping("/limits/{userId}")
    public ResponseEntity<LimitsResponse> getLimits(@PathVariable Long userId) {
        Subscription subscription = subscriptionService.getCurrentSubscription(userId);
        Plan plan = subscription.getPlan();
        return ResponseEntity.ok(new LimitsResponse(plan.getLimits()));
    }

    @PostMapping("/limits/check")
    public ResponseEntity<LimitsCheckResponse> checkLimits(@Valid @RequestBody LimitsCheckRequest request) {
        Subscription subscription = subscriptionService.getCurrentSubscription(request.userId());
        Plan plan = subscription.getPlan();
        Map<String, Integer> planLimits = plan.getLimits();
        
        boolean allowed = request.requestedLimits().entrySet().stream()
                .allMatch(entry -> {
                    Integer limitValue = planLimits.getOrDefault(entry.getKey(), 0);
                    return entry.getValue() <= limitValue;
                });
                
        return ResponseEntity.ok(new LimitsCheckResponse(allowed));
    }
}
