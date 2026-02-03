package com.javarush.entity.slide5;

import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name = "slide5_employees")
@Data
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true) // Разрешаем null для демонстрации
    private String name;

    @Column(nullable = false)
    private String occupation;

    @Column(nullable = false)
    private Double salary;
}