package com.javarush.entity.slide14;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "slide14_book_details")
@Data
public class BookDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(nullable = false)
    private String genre;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "publication_year", nullable = false)
    private Integer publicationYear;

    @Column(name = "page_count")
    private Integer pageCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;
}