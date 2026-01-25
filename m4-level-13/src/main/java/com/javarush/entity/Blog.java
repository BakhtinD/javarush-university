package com.javarush.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "slide20_blog")
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;

    // EAGER по умолчанию для @OneToMany, но явно указываем для наглядности
    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<BlogPost> posts = new ArrayList<>();

    // Демонстрация LAZY для @OneToMany
    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<BlogComment> comments = new ArrayList<>();

    public Blog(String title, String author) {
        this.title = title;
        this.author = author;
    }

    public void addPost(BlogPost post) {
        posts.add(post);
        post.setBlog(this);
    }

    public void addComment(BlogComment comment) {
        comments.add(comment);
        comment.setBlog(this);
    }
}