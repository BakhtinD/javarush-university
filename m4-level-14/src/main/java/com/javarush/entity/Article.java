package com.javarush.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.EXTRA)
    @OrderColumn(name = "tag_order")
    private List<Tag> tags = new ArrayList<>();

    public void addTag(Tag tag) {
        tag.setArticle(this);
        tags.add(tag);
    }
}