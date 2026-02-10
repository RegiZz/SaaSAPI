package pl.regizz.saasapi.domain.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "plans")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private BillingPeriod billingPeriod;

    @ElementCollection
    @CollectionTable(name = "plan_limits", joinColumns = @JoinColumn(name = "plan_id"))
    @MapKeyColumn(name = "limit_name")
    @Column(name = "limit_value")
    private Map<String, Integer> limits = new HashMap<>();

    private boolean active = true;

    protected Plan() {}

    public Plan(String code, BigDecimal price, BillingPeriod billingPeriod, Map<String, Integer> limits) {
        this.code = code;
        this.price = price;
        this.billingPeriod = billingPeriod;
        this.limits = limits != null ? limits : new HashMap<>();
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BillingPeriod getBillingPeriod() {
        return billingPeriod;
    }

    public Map<String, Integer> getLimits() {
        return limits;
    }

    public boolean isActive() {
        return active;
    }

    public void updatePlan(BigDecimal price, BillingPeriod billingPeriod, Map<String, Integer> limits, boolean active) {
        this.price = price;
        this.billingPeriod = billingPeriod;
        this.limits = limits != null ? limits : new HashMap<>();
        this.active = active;
    }
}
