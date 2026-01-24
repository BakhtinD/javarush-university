package com.javarush.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "tag_order")
    private Integer order;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;
}