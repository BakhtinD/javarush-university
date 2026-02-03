package com.javarush.entity.slide4;

import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name = "slide4_products")
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private String category;

    @Column(name = "in_stock")
    private Boolean inStock;
}