package pl.regizz.saasapi.domain.event;

import jakarta.persistence.*;
import pl.regizz.saasapi.domain.model.Subscription;

import java.time.Instant;

@Entity
@Table(name = "subscription_events")
public class SubscriptionEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Subscription subscription;

    @Enumerated(EnumType.STRING)
    private SubscriptionEventType type;

    private Instant occurredAt;

    protected SubscriptionEvent() {
    }

    public SubscriptionEvent(Subscription subscription, SubscriptionEventType type, Instant occurredAt) {
        this.subscription = subscription;
        this.type = type;
        this.occurredAt = occurredAt;
    }

    public Long getId() {
        return id;
    }
}