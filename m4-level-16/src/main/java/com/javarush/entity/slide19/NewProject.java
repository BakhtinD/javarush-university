package com.javarush.entity.slide19;

import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name = "slide19_new_projects")
@Data
public class NewProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_name", nullable = false, unique = true)
    private String name;

    @Column(name = "project_budget")
    private Integer budget;
}