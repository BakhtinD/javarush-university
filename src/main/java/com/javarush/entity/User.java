package com.javarush.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
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

    @Column(name = "user_name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name= "level")
    private String level;

    @Column(name = "active")
    private boolean active;

    @Column(name = "score")
    private Double score;

    @Column(name = "salary")
    private BigDecimal salary;

    @Column(name = "birth_day")
    private LocalDate birthDay;

    @Column(name = "registration_date")
    private Date registrationDate;

    @Column(name = "avatar")
    private byte[] avatar;

    @ElementCollection
    @CollectionTable(name = "user_messages", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "message")
    private List<String> messages = new ArrayList<>();

    public User(String name, String email, String level, boolean active,
                Double score, BigDecimal salary, LocalDate birthDay,
                Date registrationDate, byte[] avatar) {
        this.name = name;
        this.email = email;
        this.level = level;
        this.active = active;
        this.score = score;
        this.salary = salary;
        this.birthDay = birthDay;
        this.registrationDate = registrationDate;
        this.avatar = avatar;
        this.messages = messages;
    }
}
