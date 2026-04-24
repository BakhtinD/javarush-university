package com.javarush.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "AUTHORS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// Исключаем коллекцию books из toString и equals/hashCode,
// чтобы избежать StackOverflowError при двунаправленных связях
@ToString(exclude = "books")
@EqualsAndHashCode(exclude = "books")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String bio;

    // LAZY — книги не подгружаются автоматически.
    // Для разрешения поля Author.books используется @SchemaMapping в контроллере.
    // Это ключевой момент для понимания Data Fetcher и проблемы N+1.
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Book> books = new ArrayList<>();

    public Author(String name, String bio) {
        this.name = name;
        this.bio = bio;
    }
}
