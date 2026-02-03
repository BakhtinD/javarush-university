package com.javarush.entity.slide9;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "slide9_sales")
@Data
public class SalesRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "salesperson", nullable = false)
    private String salesperson;

    @Column(name = "region", nullable = false)
    private String region; // "NORTH", "SOUTH", "EAST", "WEST"

    @Column(name = "sale_amount", nullable = false)
    private BigDecimal saleAmount;

    @Column(name = "commission_rate")
    private BigDecimal commissionRate; // Может быть NULL

    @Column(name = "sale_date", nullable = false)
    private LocalDate saleDate;

    @Column(name = "product_category", nullable = false)
    private String productCategory; // "ELECTRONICS", "FURNITURE", "CLOTHING"
}