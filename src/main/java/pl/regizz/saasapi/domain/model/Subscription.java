package pl.regizz.saasapi.domain.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Plan plan;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    private Instant startDate;
    private Instant endDate;
    private Instant trialEndDate;

    private boolean autoRenew;

    protected Subscription() {}

    private Subscription(User user, Plan plan, Instant trialEndDate) {
        this.user = user;
        this.plan = plan;
        this.status = SubscriptionStatus.TRIAL;
        this.trialEndDate = trialEndDate;
        this.startDate = Instant.now();
        this.autoRenew = true;
    }

    public static Subscription startTrial(User user, Plan plan, Instant trialEndDate) {
        return new Subscription(user, plan, trialEndDate);
    }

    public void activate() {
        if(status != SubscriptionStatus.TRIAL) {
            throw new IllegalStateException("Subscription is not in trial state");
        }

        this.status = SubscriptionStatus.ACTIVE;
    }

    public void cancel() {
        this.status = SubscriptionStatus.CANCELLED;
        this.autoRenew = false;
        this.endDate = Instant.now();
    }

    public void expire() {
        this.status = SubscriptionStatus.EXPIRED;
        this.endDate = Instant.now();
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public Instant getTrialEndDate(){
        return trialEndDate;
    }
}
