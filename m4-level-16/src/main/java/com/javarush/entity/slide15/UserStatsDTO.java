package com.javarush.entity.slide15;

import lombok.Data;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

// Обычный Java-класс без аннотаций JPA
@Data
public class UserStatsDTO {
    // Поля должны совпадать с именами колонок в SQL
    private Long userId;
    private String userName;
    private String userEmail;
    private BigInteger totalOrders;
    private BigDecimal totalSpent;
    private LocalDate lastOrderDate;
    private String mostPurchasedCategory;
}