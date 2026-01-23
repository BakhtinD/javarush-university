package com.javarush.entity;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "description")
    private String description;

    @Column(name = "deadline")
    private LocalDate deadline;

    // Many-to-One: много задач -> один работник
    @ManyToOne
    @JoinColumn(name = "worker_id")
    private Worker worker;

    public Task(String description, LocalDate deadline) {
        this.description = description;
        this.deadline = deadline;
    }
}