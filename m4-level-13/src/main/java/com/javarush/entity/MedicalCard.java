package com.javarush.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_cards")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "patient")
public class MedicalCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "blood_type")
    private String bloodType;

    @Column(name = "allergies")
    private String allergies;

    @Column(name = "last_checkup_date")
    private LocalDateTime lastCheckupDate;

    // Отображаемая сторона: использует mappedBy
    @OneToOne(mappedBy = "medicalCard")
    private Patient patient;

    public MedicalCard(String bloodType, String allergies, LocalDateTime lastCheckupDate) {
        this.bloodType = bloodType;
        this.allergies = allergies;
        this.lastCheckupDate = lastCheckupDate;
    }
}