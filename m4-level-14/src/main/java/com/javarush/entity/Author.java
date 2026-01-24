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
@ToString(exclude = {"books", "publisher"})
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // OneToMany: LAZY по умолчанию (не указан fetch)
    @OneToMany(mappedBy = "author")
    private List<Book> books = new ArrayList<>();

    // ManyToOne: EAGER по умолчанию (не указан fetch)
    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;
}
