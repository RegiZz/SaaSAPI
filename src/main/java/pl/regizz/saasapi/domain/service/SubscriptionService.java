package pl.regizz.saasapi.domain.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.regizz.saasapi.domain.model.Plan;
import pl.regizz.saasapi.domain.model.Subscription;
import pl.regizz.saasapi.domain.model.SubscriptionStatus;
import pl.regizz.saasapi.domain.model.User;
import pl.regizz.saasapi.infrastructure.persistence.SubscriptionRepository;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public Subscription startTrial(User user, Plan plan, Instant trialEndDate) {
        subscriptionRepository.findByUserAndStatusIn(
                user,
                List.of(SubscriptionStatus.TRIAL, SubscriptionStatus.ACTIVE)
        ).ifPresent(s -> {
            throw new IllegalStateException("User already has active subscription");
        });

        Subscription subscription = Subscription.startTrial(user, plan, trialEndDate);
        return subscriptionRepository.save(subscription);
    }

    public void cancelSubscription(Subscription subscription) {
        subscription.cancel();
    }

    public void activateTrial(Subscription subscription) {
        subscription.activate();
    }

    public void expireSubscription(Subscription subscription) {
        subscription.expire();
    }

}
