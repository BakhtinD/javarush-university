package com.javarush.entity.joinedtablestrategy;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "accounts")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", unique = true, nullable = false)
    private String accountNumber;

    @Column(name = "account_holder", nullable = false)
    private String accountHolder;

    @Column(name = "opening_date")
    private LocalDate openingDate;

    @Column(name = "balance", nullable = false)
    private Double balance = 0.0;

    @Column(name = "currency")
    private String currency = "RUB";

    @Column(name = "is_active")
    private Boolean isActive = true;
}