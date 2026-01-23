package com.javarush.entity;

import lombok.*;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "workers")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    // One-to-Many: один работник -> много задач
    @OneToMany(mappedBy = "worker")
    private List<Task> tasks;

    public Worker(String name) {
        this.name = name;
    }
}