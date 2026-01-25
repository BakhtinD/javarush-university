package com.javarush.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "medical_card")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "insurance_number", unique = true)
    private String insuranceNumber;

    // Владеющая сторона: имеет @JoinColumn
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "medical_card_id")
    private MedicalCard medicalCard;

    public Patient(String fullName, LocalDate dateOfBirth, String insuranceNumber) {
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.insuranceNumber = insuranceNumber;
    }
}