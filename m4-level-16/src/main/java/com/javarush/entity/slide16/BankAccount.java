package com.javarush.entity.slide16;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "slide16_bank_accounts")
@Data
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", unique = true, nullable = false)
    private String accountNumber;

    @Column(name = "account_holder", nullable = false)
    private String accountHolder;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(name = "currency", nullable = false)
    private String currency; // "USD", "EUR", etc.
}