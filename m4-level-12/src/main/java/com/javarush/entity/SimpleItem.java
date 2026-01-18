package com.javarush.entity;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "simple_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SimpleItem {

    // Пример 1: Простой Integer ID
    @Id
    private Integer id;

    @Column(name = "name")
    private String name;

    public SimpleItem(String name) {
        this.name = name;
    }
}