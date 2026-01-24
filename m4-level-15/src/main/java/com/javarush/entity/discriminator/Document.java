package com.javarush.entity.discriminator;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Table(name = "documents")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
// Вариант 1: Строковый дискриминатор (по умолчанию)
@DiscriminatorColumn(name = "doc_type", discriminatorType = DiscriminatorType.STRING)

// Вариант 2: Числовой дискриминатор (раскомментировать для демонстрации)
// @DiscriminatorColumn(name = "doc_type_code", discriminatorType = DiscriminatorType.INTEGER)

// Вариант 3: Символьный дискриминатор
// @DiscriminatorColumn(name = "doc_type_char", discriminatorType = DiscriminatorType.CHAR, length = 1)
@Getter
@Setter
public abstract class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "author")
    private String author;

    @Column(name = "created_date")
    private java.time.LocalDate createdDate;

    @Column(name = "file_size")
    private Long fileSize; // в байтах
}