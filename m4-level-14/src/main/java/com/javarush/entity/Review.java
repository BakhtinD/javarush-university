package com.javarush.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    private int rating;

    // ManyToOne: EAGER по умолчанию
    @ManyToOne
    @JoinColumn(name = "book_id")
    @ToString.Exclude
    private Book book;
}