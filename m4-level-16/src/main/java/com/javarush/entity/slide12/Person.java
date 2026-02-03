package com.javarush.entity.slide12;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "slide12_persons")
@Data
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private String city;

    @Column(name = "registration_date", nullable = false)
    private LocalDate registrationDate;

    @Column(name = "email")
    private String email; // Может быть null
}