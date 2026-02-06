package pl.regizz.saasapi.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.regizz.saasapi.domain.event.SubscriptionEvent;

public interface SubscriptionEventRepository extends JpaRepository<SubscriptionEvent, Long> {
}