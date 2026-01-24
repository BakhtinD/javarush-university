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
@ToString(exclude = {"author", "reviews"})
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    // ManyToOne: EAGER по умолчанию
    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    // OneToMany: LAZY по умолчанию
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    public void addReview(Review review) {
        review.setBook(this);
        reviews.add(review);
    }
}
