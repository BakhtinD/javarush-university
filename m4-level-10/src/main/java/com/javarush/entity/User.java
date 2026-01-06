package com.javarush.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_name", unique = true, length = 100)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "level")
    private Integer level;

    // Конструктор для удобства (без id, т.к. он генерируется автоматически)
    public User(String name, String email, Integer level) {
        this.name = name;
        this.email = email;
        this.level = level;
    }
}