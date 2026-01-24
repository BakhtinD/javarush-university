package com.javarush.entity;

import javax.persistence.*;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    public Category() {}

    public Category(String name) {
        this.name = name;
    }

    // геттеры/сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}