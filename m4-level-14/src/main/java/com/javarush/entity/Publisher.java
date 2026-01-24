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
@ToString(exclude = "authors")
public class Publisher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // OneToMany: LAZY по умолчанию
    @OneToMany(mappedBy = "publisher")
    private List<Author> authors = new ArrayList<>();
}
