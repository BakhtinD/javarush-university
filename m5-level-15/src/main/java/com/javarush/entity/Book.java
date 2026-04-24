package com.javarush.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "BOOKS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    // EAGER — автор загружается вместе с книгой.
    // GraphQL автоматически резолвит поле Book.author через геттер getAuthor().
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    public Book(String title, Author author) {
        this.title = title;
        this.author = author;
    }
}
