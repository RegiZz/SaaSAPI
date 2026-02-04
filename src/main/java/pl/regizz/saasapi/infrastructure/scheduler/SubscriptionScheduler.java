package pl.regizz.saasapi.infrastructure.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.regizz.saasapi.domain.model.Subscription;
import pl.regizz.saasapi.domain.model.SubscriptionStatus;
import pl.regizz.saasapi.infrastructure.persistence.SubscriptionRepository;

import java.time.Instant;
import java.util.List;

@Component
public class SubscriptionScheduler {

    private final SubscriptionRepository repository;

    public SubscriptionScheduler(SubscriptionRepository repository) {
        this.repository = repository;
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void processTrials(){
        List<Subscription> trials = repository.findAll().stream()
                .filter(s -> s.getStatus() == SubscriptionStatus.TRIAL)
                .filter(s -> s.getTrialEndDate().isBefore(Instant.now()))
                .toList();

        trials.forEach(Subscription::expire);
    }
}
