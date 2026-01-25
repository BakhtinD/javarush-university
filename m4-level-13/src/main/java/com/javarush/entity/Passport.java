package com.javarush.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "passports")
@Getter
@Setter
@NoArgsConstructor
public class Passport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String number;

    private String country;

    // Односторонняя связь: паспорт знает о человеке
    @OneToOne
    @JoinColumn(name = "person_id")
    private Person person;

    public Passport(String number, String country) {
        this.number = number;
        this.country = country;
    }
}