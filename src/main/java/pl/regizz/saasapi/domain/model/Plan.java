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

    private boolean active = true;

    protected Plan() {}

    public Plan(String code, BigDecimal price, BillingPeriod billingPeriod) {
        this.code = code;
        this.price = price;
        this.billingPeriod = billingPeriod;
    }

    public Long getId() {
        return id;
    }
}
