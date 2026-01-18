package com.javarush.entity;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "uuid_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UuidItem {

    // Пример 2: UUID как ID
    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private String id; // Храним UUID как строку

    @Column(name = "name")
    private String name;

    // Конструктор с генерацией UUID
    public UuidItem(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }
}