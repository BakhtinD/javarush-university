package com.javarush.entity.slide17;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "slide17_audit_log")
@Data
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "action_type", nullable = false)
    private String actionType; // "CREATE", "UPDATE", "DELETE", "TRANSACTION_TEST"

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "transaction_status")
    private String transactionStatus; // "STARTED", "COMMITTED", "ROLLED_BACK", "TIMED_OUT"
}