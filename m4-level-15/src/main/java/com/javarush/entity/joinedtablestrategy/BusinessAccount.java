package com.javarush.entity.joinedtablestrategy;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Table(name = "business_accounts")
@PrimaryKeyJoinColumn(name = "account_id")
@Getter
@Setter
public class BusinessAccount extends Account {
    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "tax_id_number")
    private String taxIdNumber;

    @Column(name = "business_type")
    private String businessType; // LLC, CORPORATION, PARTNERSHIP

    @Column(name = "authorized_signers_count")
    private Integer authorizedSignersCount = 1;

    @Column(name = "commercial_loan_eligible")
    private Boolean commercialLoanEligible = false;

    @Column(name = "merchant_services_enabled")
    private Boolean merchantServicesEnabled = false;
}