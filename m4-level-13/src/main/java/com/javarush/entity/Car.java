package com.javarush.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "cars")
@Getter
@Setter
@NoArgsConstructor
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String model;

    private String color;

    @OneToOne(mappedBy = "car", cascade = CascadeType.ALL)
    private Engine engine;

    public Car(String model, String color) {
        this.model = model;
        this.color = color;
    }
}