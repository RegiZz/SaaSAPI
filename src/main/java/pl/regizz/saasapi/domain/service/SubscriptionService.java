package pl.regizz.saasapi.domain.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.regizz.saasapi.domain.event.SubscriptionEvent;
import pl.regizz.saasapi.domain.event.SubscriptionEventType;
import pl.regizz.saasapi.domain.model.BillingPeriod;
import pl.regizz.saasapi.domain.model.Plan;
import pl.regizz.saasapi.domain.model.Subscription;
import pl.regizz.saasapi.domain.model.SubscriptionStatus;
import pl.regizz.saasapi.domain.model.User;
import pl.regizz.saasapi.exception.NotFoundException;
import pl.regizz.saasapi.infrastructure.persistence.PlanRepository;
import pl.regizz.saasapi.infrastructure.persistence.SubscriptionEventRepository;
import pl.regizz.saasapi.infrastructure.persistence.SubscriptionRepository;
import pl.regizz.saasapi.infrastructure.persistence.UserRepository;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@Transactional
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final SubscriptionEventRepository eventRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository,
                               UserRepository userRepository,
                               PlanRepository planRepository,
                               SubscriptionEventRepository eventRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.planRepository = planRepository;
        this.eventRepository = eventRepository;
    }

    public Subscription startTrial(Long userId, Long planId, Instant trialEndDate, boolean autoRenew) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new NotFoundException("Plan not found"));

        subscriptionRepository.findByUserAndStatusIn(
                user,
                List.of(SubscriptionStatus.TRIAL, SubscriptionStatus.ACTIVE)
        ).ifPresent(s -> {
            throw new IllegalStateException("User already has active subscription");
        });

        Subscription subscription = Subscription.startTrial(user, plan, trialEndDate, autoRenew);
        Subscription saved = subscriptionRepository.save(subscription);
        eventRepository.save(new SubscriptionEvent(saved, SubscriptionEventType.TRIAL_STARTED, Instant.now()));
        return saved;
    }

    public Subscription cancelSubscription(Long subscriptionId) {
        Subscription subscription = findSubscription(subscriptionId);
        subscription.cancel();
        eventRepository.save(new SubscriptionEvent(subscription, SubscriptionEventType.SUBSCRIPTION_CANCELED, Instant.now()));
        return subscription;
    }

    public Subscription activateTrial(Subscription subscription) {
        subscription.activate();
        eventRepository.save(new SubscriptionEvent(subscription, SubscriptionEventType.TRIAL_ACTIVATED, Instant.now()));
        return subscription;
    }

    public Subscription expireSubscription(Subscription subscription) {
        subscription.expire();
        eventRepository.save(new SubscriptionEvent(subscription, SubscriptionEventType.SUBSCRIPTION_EXPIRED, Instant.now()));
        return subscription;
    }

    public Subscription changePlan(Long subscriptionId, Long newPlanId) {
        Subscription subscription = findSubscription(subscriptionId);
        Plan newPlan = planRepository.findById(newPlanId)
                .orElseThrow(() -> new NotFoundException("Plan not found"));

        Plan currentPlan = subscription.getPlan();
        if (currentPlan.getPrice().compareTo(newPlan.getPrice()) < 0) {
            subscription.changePlanImmediately(newPlan, Instant.now());
        } else if (currentPlan.getPrice().compareTo(newPlan.getPrice()) > 0) {
            Instant changeDate = nextBillingDate(subscription);
            subscription.scheduleDowngrade(newPlan, changeDate);
        }

        eventRepository.save(new SubscriptionEvent(subscription, SubscriptionEventType.PLAN_CHANGED, Instant.now()));
        return subscription;
    }

    public Subscription getCurrentSubscription(Long userId) {
        return subscriptionRepository.findFirstByUserIdAndStatusIn(
                userId,
                List.of(SubscriptionStatus.TRIAL, SubscriptionStatus.ACTIVE)
        ).orElseThrow(() -> new NotFoundException("Subscription not found"));
    }

    public List<Subscription> processTrials(Instant now) {
        List<Subscription> trials = subscriptionRepository.findByStatusAndTrialEndDateBefore(SubscriptionStatus.TRIAL, now);
        for (Subscription trial : trials) {
            if (trial.isAutoRenew()) {
                activateTrial(trial);
            } else {
                expireSubscription(trial);
            }
        }
        return trials;
    }

    public List<Subscription> processPendingPlanChanges(Instant now) {
        List<Subscription> pending = subscriptionRepository.findByPendingPlanChangeDateBefore(now);
        for (Subscription subscription : pending) {
            if (subscription.applyPendingPlanChange(now)) {
                eventRepository.save(new SubscriptionEvent(subscription, SubscriptionEventType.PLAN_CHANGED, now));
            }
        }
        return pending;
    }

    private Subscription findSubscription(Long subscriptionId) {
        return subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new NotFoundException("Subscription not found"));
    }

    private Instant nextBillingDate(Subscription subscription) {
        BillingPeriod billingPeriod = subscription.getPlan().getBillingPeriod();
        ZonedDateTime start = ZonedDateTime.ofInstant(subscription.getStartDate(), ZoneOffset.UTC);
        ZonedDateTime next = billingPeriod == BillingPeriod.YEARLY ? start.plusYears(1) : start.plusMonths(1);
        return next.toInstant();
    }

}
