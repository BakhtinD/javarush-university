package com.javarush.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "products")
@Getter @Setter @NoArgsConstructor @ToString
@NamedQueries({
        @NamedQuery(name = "Product.findAll", query = "from Product")
})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String category;
    private Double price;
    private Integer stock;

    public Product(String name, String category, Double price, Integer stock) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }
}