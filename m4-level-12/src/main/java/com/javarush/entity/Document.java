package com.javarush.entity;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    // Пример 1: Boolean -> 'Y'/'N'
    @Column(name = "is_signed", length = 1)
    @Type(type = "yes_no") // 'Y' или 'N'
    private Boolean isSigned;

    // Пример 2: Boolean -> 1/0
    @Column(name = "is_archived")
    @Type(type = "numeric_boolean") // 1 или 0
    private Boolean isArchived;

    // Пример 3: Boolean -> true/false (стандартный BIT)
    @Column(name = "is_public")
    private Boolean isPublic;

    // Пример 4: LocalDate (стандартный маппинг)
    @Column(name = "created_date")
    private LocalDate createdDate;

    public Document(String title,
                    Boolean isSigned,
                    Boolean isArchived,
                    Boolean isPublic,
                    LocalDate createdDate) {
        this.title = title;
        this.isSigned = isSigned;
        this.isArchived = isArchived;
        this.isPublic = isPublic;
        this.createdDate = createdDate;
    }
}