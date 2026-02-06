package pl.regizz.saasapi.infrastructure.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.regizz.saasapi.domain.service.SubscriptionService;

import java.time.Instant;

@Component
public class SubscriptionScheduler {

    private final SubscriptionService subscriptionService;

    public SubscriptionScheduler(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void processTrials(){
        Instant now = Instant.now();
        subscriptionService.processTrials(now);
        subscriptionService.processPendingPlanChanges(now);
    }
}
