package com.javarush.entity.slide8;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "slide8_projects")
@Data
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Column(name = "client_name", nullable = false)
    private String clientName;

    @Column(nullable = false)
    private BigDecimal budget;

    @Column(name = "team_size", nullable = false)
    private Integer teamSize;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = true)
    private LocalDate endDate;

    @Column(name = "status", nullable = false)
    private String status; // "PLANNING", "ACTIVE", "COMPLETED", "CANCELLED"

    @Column(name = "priority", nullable = false)
    private Integer priority; // 1 (highest) to 5 (lowest)
}