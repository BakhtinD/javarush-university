package com.javarush.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "persons")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "passport")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // Двусторонняя связь
    @OneToOne(mappedBy = "person", cascade = CascadeType.ALL)
    private Passport passport;

    public Person(String name) {
        this.name = name;
    }
}