package com.javarush.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "book_details")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class BookDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "isbn")
    private String isbn;

    @Column(name = "page_count")
    private Integer pageCount;

    // One-to-One: одна деталь -> одна книга
    @OneToOne
    @JoinColumn(name = "book_id")
    private Book book;

    public BookDetail(String isbn, Integer pageCount) {
        this.isbn = isbn;
        this.pageCount = pageCount;
    }
}