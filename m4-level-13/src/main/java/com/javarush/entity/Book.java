package com.javarush.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title")
    private String title;

    // Many-to-One: много книг -> один автор
    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    public Book(String title) {
        this.title = title;
    }
}