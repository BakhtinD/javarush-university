package com.javarush.entity.slide14;

import lombok.Data;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "slide14_authors")
@Data
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "nationality", nullable = false)
    private String nationality;

    @Column(name = "email")
    private String email;

    // Связь будет заполнена через NativeQuery
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<BookDetail> books = new ArrayList<>();
}

