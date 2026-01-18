package com.javarush.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "products_with_enum")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductWithEnum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    // Пример 1: Enum как ORDINAL (число)
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "category_ordinal")
    private ProductCategory categoryOrdinal;

    // Пример 2: Enum как STRING (текст)
    @Enumerated(EnumType.STRING)
    @Column(name = "category_string", length = 20)
    private ProductCategory categoryString;

    public ProductWithEnum(String name,
                           ProductCategory categoryOrdinal,
                           ProductCategory categoryString) {
        this.name = name;
        this.categoryOrdinal = categoryOrdinal;
        this.categoryString = categoryString;
    }
}