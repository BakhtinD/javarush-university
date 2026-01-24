package com.javarush.entity.joinedtablestrategy;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Table(name = "checking_accounts")
@PrimaryKeyJoinColumn(name = "account_id")
@Getter
@Setter
public class CheckingAccount extends Account {
    @Column(name = "overdraft_limit")
    private Double overdraftLimit;

    @Column(name = "monthly_fee")
    private Double monthlyFee;

    @Column(name = "checkbook_available")
    private Boolean checkbookAvailable = true;

    @Column(name = "atm_fee_waived")
    private Boolean atmFeeWaived = false;

    @Column(name = "transaction_limit_per_day")
    private Integer transactionLimitPerDay;
}