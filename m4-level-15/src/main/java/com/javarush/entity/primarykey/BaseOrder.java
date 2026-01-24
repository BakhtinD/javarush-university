package com.javarush.entity.primarykey;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "base_orders")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class BaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id") // Явно указываем имя колонки
    private Long orderId;

    @Column(name = "order_number", unique = true, nullable = false)
    private String orderNumber;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Column(name = "status")
    private String status = "NEW";

    @PrePersist
    protected void onCreate() {
        orderDate = LocalDateTime.now();
    }
}