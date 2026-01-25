package com.javarush.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "slide20_blog_comment")
public class BlogComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String author;
    private String text;
    private LocalDateTime createdAt;

    // EAGER по умолчанию для @ManyToOne, но оставляем по умолчанию для демонстрации
    @ManyToOne
    @JoinColumn(name = "blog_id")
    private Blog blog;

    public BlogComment(String author, String text) {
        this.author = author;
        this.text = text;
        this.createdAt = LocalDateTime.now();
    }
}