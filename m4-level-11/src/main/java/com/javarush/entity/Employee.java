package com.javarush.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "position")
    private String position;

    @Column(name = "salary")
    private Integer salary;

    // Обратная связь (опционально)
    @OneToMany(mappedBy = "employee")
    @ToString.Exclude // чтобы избежать циклического вызова toString
    private List<EmployeeTask> tasks;

    public Employee(String name, String position, Integer salary) {
        this.name = name;
        this.position = position;
        this.salary = salary;
    }
}