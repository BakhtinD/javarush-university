package com.javarush.entity.slide3;

import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name = "slide3_customers")
@Data // Lombok: геттеры, сеттеры, toString, equals, hashCode
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private Integer loyaltyPoints; // Баллы лояльности
}