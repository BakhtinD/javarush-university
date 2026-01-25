package com.javarush.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "slide20_blog_post")
public class BlogPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private LocalDateTime publishedDate;

    // LAZY по умолчанию для @ManyToOne, но явно указываем для наглядности
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id")
    private Blog blog;

    public BlogPost(String title, String content) {
        this.title = title;
        this.content = content;
        this.publishedDate = LocalDateTime.now();
    }
}