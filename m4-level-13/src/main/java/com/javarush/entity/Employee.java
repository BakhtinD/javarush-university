package com.javarush.entity;

import lombok.*;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    // List<String> - упорядоченная коллекция
    @ElementCollection
    @CollectionTable(name = "employee_skills",
            joinColumns = @JoinColumn(name = "employee_id"))
    @OrderColumn(name = "skill_order")
    @Column(name = "skill")
    private List<String> skills = new ArrayList<>();

    // Set<String> - неупорядоченная коллекция (уникальные значения)
    @ElementCollection
    @CollectionTable(name = "employee_languages",
            joinColumns = @JoinColumn(name = "employee_id"))
    @Column(name = "language")
    private Set<String> languages = new HashSet<>();

    public Employee(String name) {
        this.name = name;
    }
}