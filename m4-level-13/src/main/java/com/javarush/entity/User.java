package com.javarush.entity;

import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "active")
    private boolean active;

    @Column(name = "score")
    private Double score;

    @Column(name = "salary", precision = 10, scale = 2)
    private BigDecimal salary;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "registration_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date registrationDate;

    @Column(name = "avatar")
    private byte[] avatar;

    // НОВОЕ: Коллекция строк (сообщений пользователя)
    @ElementCollection
    @CollectionTable(name = "user_messages", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "message")
    private List<String> messages = new ArrayList<>();

    public User(String name, String email, Integer level,
                boolean active, Double score, BigDecimal salary,
                LocalDate birthDate, Date registrationDate, byte[] avatar) {
        this.name = name;
        this.email = email;
        this.level = level;
        this.active = active;
        this.score = score;
        this.salary = salary;
        this.birthDate = birthDate;
        this.registrationDate = registrationDate;
        this.avatar = avatar;
    }
}