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

    @ManyToOne
    private Plan pendingPlan;

    private Instant pendingPlanChangeDate;

    private boolean autoRenew;

    protected Subscription() {}

    private Subscription(User user, Plan plan, Instant trialEndDate, boolean autoRenew) {
        this.user = user;
        this.plan = plan;
        this.status = SubscriptionStatus.TRIAL;
        this.trialEndDate = trialEndDate;
        this.startDate = Instant.now();
        this.autoRenew = autoRenew;
    }

    public static Subscription startTrial(User user, Plan plan, Instant trialEndDate) {
        return new Subscription(user, plan, trialEndDate, true);
    }

    public static Subscription startTrial(User user, Plan plan, Instant trialEndDate, boolean autoRenew) {
        return new Subscription(user, plan, trialEndDate, autoRenew);
    }

    public void activate() {
        if(status != SubscriptionStatus.TRIAL) {
            throw new IllegalStateException("Subscription is not in trial state");
        }

        this.status = SubscriptionStatus.ACTIVE;
    }

    public void changePlanImmediately(Plan newPlan, Instant changeDate) {
        this.plan = newPlan;
        this.pendingPlan = null;
        this.pendingPlanChangeDate = null;
        this.startDate = changeDate;
    }

    public void scheduleDowngrade(Plan newPlan, Instant changeDate) {
        this.pendingPlan = newPlan;
        this.pendingPlanChangeDate = changeDate;
    }

    public boolean applyPendingPlanChange(Instant now) {
        if (pendingPlan == null || pendingPlanChangeDate == null) {
            return false;
        }
        if (now.isBefore(pendingPlanChangeDate)) {
            return false;
        }

        this.plan = pendingPlan;
        this.startDate = pendingPlanChangeDate;
        this.pendingPlan = null;
        this.pendingPlanChangeDate = null;
        return true;
    }

    public void cancel() {
        this.status = SubscriptionStatus.CANCELED;
        this.autoRenew = false;
        this.endDate = Instant.now();
    }

    public void expire() {
        this.status = SubscriptionStatus.EXPIRED;
        this.endDate = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Plan getPlan() {
        return plan;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public boolean isAutoRenew() {
        return autoRenew;
    }

    public Plan getPendingPlan() {
        return pendingPlan;
    }

    public Instant getPendingPlanChangeDate() {
        return pendingPlanChangeDate;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public Instant getTrialEndDate(){
        return trialEndDate;
    }
}
