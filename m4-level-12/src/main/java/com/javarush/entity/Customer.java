package com.javarush.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Встроенный объект FullName
    @Embedded
    private FullName fullName;

    // Встроенный объект Address (основной адрес)
    @Embedded
    private Address address;

    // Второй Address с переопределением колонок
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "country", column = @Column(name = "delivery_country")),
            @AttributeOverride(name = "city", column = @Column(name = "delivery_city")),
            @AttributeOverride(name = "street", column = @Column(name = "delivery_street")),
            @AttributeOverride(name = "houseNumber", column = @Column(name = "delivery_house_number")),
            @AttributeOverride(name = "zipCode", column = @Column(name = "delivery_zip_code"))
    })
    private Address deliveryAddress;

    @Column(name = "email")
    private String email;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    public Customer(FullName fullName, Address address, Address deliveryAddress,
                    String email, LocalDate registrationDate) {
        this.fullName = fullName;
        this.address = address;
        this.deliveryAddress = deliveryAddress;
        this.email = email;
        this.registrationDate = registrationDate;
    }
}