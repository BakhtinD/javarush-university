package com.javarush.entity.tableperclass;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Table(name = "books")
@Getter
@Setter
public class Book extends MediaItem {
    @Column(name = "author")
    private String author;

    @Column(name = "isbn", unique = true)
    private String isbn;

    @Column(name = "page_count")
    private Integer pageCount;

    @Column(name = "publisher")
    private String publisher;

    @Column(name = "edition")
    private Integer edition = 1;

    @Column(name = "translator")
    private String translator;

    @Column(name = "has_illustrations")
    private Boolean hasIllustrations = false;
}