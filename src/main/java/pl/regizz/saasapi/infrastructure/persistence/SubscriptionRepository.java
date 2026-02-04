package pl.regizz.saasapi.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.regizz.saasapi.domain.model.Subscription;
import pl.regizz.saasapi.domain.model.SubscriptionStatus;
import pl.regizz.saasapi.domain.model.User;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long>{
    Optional<Subscription> findByUserAndStatusIn(
            User user,
            Iterable<SubscriptionStatus> statuses
    );
}
