package pl.regizz.saasapi.domain.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

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

    private Integer maxUsers;

    private Integer maxProjects;

    private boolean active = true;

    protected Plan() {}

    public Plan(String code, BigDecimal price, BillingPeriod billingPeriod, Integer maxUsers, Integer maxProjects) {
        this.code = code;
        this.price = price;
        this.billingPeriod = billingPeriod;
        this.maxUsers = maxUsers;
        this.maxProjects = maxProjects;
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

    public Integer getMaxUsers() {
        return maxUsers;
    }

    public Integer getMaxProjects() {
        return maxProjects;
    }

    public boolean isActive() {
        return active;
    }

    public void updatePlan(BigDecimal price, BillingPeriod billingPeriod, Integer maxUsers, Integer maxProjects, boolean active) {
        this.price = price;
        this.billingPeriod = billingPeriod;
        this.maxUsers = maxUsers;
        this.maxProjects = maxProjects;
        this.active = active;
    }
}
