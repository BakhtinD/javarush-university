package com.javarush.entity.slide7;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "slide7_accounts")
@Data
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", unique = true, nullable = false)
    private String accountNumber;

    @Column(name = "account_holder", nullable = true) // Разрешаем null
    private String accountHolder;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(name = "credit_limit", nullable = false)
    private BigDecimal creditLimit;

    @Column(name = "status", nullable = false)
    private String status; // "ACTIVE", "INACTIVE", "BLOCKED"

    @Column(name = "email", nullable = true) // Разрешаем null
    private String email;

    @Column(name = "last_activity", nullable = false)
    private LocalDateTime lastActivity;

    @Column(name = "notes", nullable = true)
    private String notes;
}