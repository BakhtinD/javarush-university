package com.javarush.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "drivers")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "license_number")
    private String licenseNumber;

    public Driver(String name, String licenseNumber) {
        this.name = name;
        this.licenseNumber = licenseNumber;
    }
}