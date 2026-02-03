package com.javarush.entity.slide6;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "slide6_developers")
@Data
public class Developer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String position; // "Junior", "Middle", "Senior", "Lead"

    @Column(nullable = false)
    private Double salary;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Column(name = "knows_java", nullable = false)
    private Boolean knowsJava;

    @Column(name = "knows_spring", nullable = false)
    private Boolean knowsSpring;
}