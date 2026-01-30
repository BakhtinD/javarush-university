package com.javarush.entity.tableperclass;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
// @Table(name = "media_items") не нужна - таблица не создается в БД
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
public abstract class MediaItem {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE) // Важно для TABLE_PER_CLASS!
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "creator")
    private String creator;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "genre")
    private String genre;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "created_at")
    private LocalDate createdAt = LocalDate.now();
}