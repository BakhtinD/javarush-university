package com.javarush.entity.slide13;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "slide13_books")
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "published_date")
    private LocalDate publishedDate;

    @Column(name = "in_stock", nullable = false)
    private Boolean inStock;
}