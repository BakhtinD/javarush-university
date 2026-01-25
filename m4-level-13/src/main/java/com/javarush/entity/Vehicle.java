package com.javarush.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "model")
    private String model;

    @Column(name = "license_plate")
    private String licensePlate;

    // Односторонний OneToOne: Vehicle знает Driver, но Driver не знает Vehicle
    @OneToOne
    @JoinColumn(name = "driver_id", unique = true) // unique = true делает связь 1:1
    private Driver driver;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    public Vehicle(String model, String licensePlate, LocalDate registrationDate) {
        this.model = model;
        this.licensePlate = licensePlate;
        this.registrationDate = registrationDate;
    }
}