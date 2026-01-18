package com.javarush.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "identity_items")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class IdentityItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    public IdentityItem(String name) {
        this.name = name;
    }
}