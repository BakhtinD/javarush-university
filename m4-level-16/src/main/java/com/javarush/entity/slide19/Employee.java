package com.javarush.entity.slide19;

import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name = "slide19_employees")
@Data
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String position;

    private Double salary;

    @Column(name = "hire_date")
    private java.time.LocalDate hireDate;

}