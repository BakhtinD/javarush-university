package com.javarush.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "USERS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String username;

    // Хранится в виде BCrypt-хэша. НИКОГДА не в открытом виде!
    @Column(nullable = false)
    private String password;

    // Роль хранится с префиксом ROLE_: "ROLE_USER" или "ROLE_ADMIN"
    @Column(nullable = false, length = 50)
    private String role;

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
