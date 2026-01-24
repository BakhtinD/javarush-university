package com.javarush.entity.joinedtablestrategy;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "savings_accounts")
@PrimaryKeyJoinColumn(name = "account_id")
@Getter
@Setter
public class SavingsAccount extends Account {
    @Column(name = "interest_rate", nullable = false)
    private BigDecimal interestRate;

    @Column(name = "minimum_balance")
    private Double minimumBalance;

    @Column(name = "interest_payment_frequency")
    private String interestPaymentFrequency; // MONTHLY, QUARTERLY, YEARLY

    @Column(name = "compounding_enabled")
    private Boolean compoundingEnabled = true;

    @Column(name = "withdrawal_limit_per_month")
    private Integer withdrawalLimitPerMonth;
}