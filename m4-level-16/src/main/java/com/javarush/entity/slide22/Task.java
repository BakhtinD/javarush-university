package com.javarush.entity.slide22;

import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name = "slide22_tasks")
@Data
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "is_completed")
    private Boolean completed = false;
}