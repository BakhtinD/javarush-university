package com.javarush.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "engines")
@Getter
@Setter
@NoArgsConstructor
public class Engine {

    @Id
    private Long id; // Не @GeneratedValue!

    private String type;

    private Integer horsepower;

    // @MapsId: Engine использует тот же ID, что и Car
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Car car;

    public Engine(String type, Integer horsepower) {
        this.type = type;
        this.horsepower = horsepower;
    }
}