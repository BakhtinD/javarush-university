package com.javarush.entity;

import lombok.*;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "articles") // исключаем articles из toString
@EqualsAndHashCode(exclude = "articles") // исключаем articles из equals/hashCode
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(cascade = CascadeType.ALL) // ← меняем на ALL
    @JoinTable(
            name = "article_tag",
            joinColumns = @JoinColumn(name = "tag_id"),
            inverseJoinColumns = @JoinColumn(name = "article_id")
    )
    private Set<Article> articles = new HashSet<>();

    public Tag(String name) {
        this.name = name;
    }
}