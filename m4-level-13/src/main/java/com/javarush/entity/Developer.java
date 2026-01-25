package com.javarush.entity;

import lombok.*;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "developers")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "projects")
@EqualsAndHashCode(exclude = "projects")
public class Developer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "developers")
    private Set<Project> projects = new HashSet<>();

    public Developer(String name) {
        this.name = name;
    }
}