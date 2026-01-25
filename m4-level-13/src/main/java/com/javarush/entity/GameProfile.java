package com.javarush.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "slide19_game_profile")
public class GameProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String avatarUrl;
    private Integer level;
    private Integer experience;

    @OneToOne
    @JoinColumn(name = "gamer_id")
    private Gamer owner;

    public GameProfile(String avatarUrl, Integer level, Integer experience) {
        this.avatarUrl = avatarUrl;
        this.level = level;
        this.experience = experience;
    }
}