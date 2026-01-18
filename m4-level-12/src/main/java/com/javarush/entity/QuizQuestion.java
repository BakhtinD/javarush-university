package com.javarush.entity;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "quiz_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "question_text", nullable = false)
    private String questionText;

    // 1. Стандартный маппинг (BIT/TINYINT)
    @Column(name = "is_active")
    private Boolean isActive;

    // 2. @Type: numeric_boolean (1/0)
    @Column(name = "is_approved")
    @Type(type = "numeric_boolean")
    private Boolean isApproved;

    // 3. @Type: yes_no ('Y'/'N')
    @Column(name = "is_verified", length = 1)
    @Type(type = "yes_no")
    private Boolean isVerified;

    // 4. @Type + columnDefinition (как в слайде)
    @Column(name = "is_correct", columnDefinition = "BIT")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean isCorrect;

    // 5. CHAR(1) с 'T'/'F' (через columnDefinition)
    @Column(name = "is_public", columnDefinition = "CHAR(1)")
    private Character isPublic; // Будем хранить как 'T'/'F'

    public QuizQuestion(String questionText,
                        Boolean isActive,
                        Boolean isApproved,
                        Boolean isVerified,
                        Boolean isCorrect,
                        Character isPublic) {
        this.questionText = questionText;
        this.isActive = isActive;
        this.isApproved = isApproved;
        this.isVerified = isVerified;
        this.isCorrect = isCorrect;
        this.isPublic = isPublic;
    }
}