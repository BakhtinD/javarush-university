package com.javarush.entity;

import lombok.*;
import org.hibernate.annotations.Formula;

import javax.persistence.*;

@Entity
@Table(name = "rectangles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Rectangle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    // 1. @Transient — не сохраняется в БД, вычисляется в Java
    @Transient
    private Integer perimeter; // Будем вычислять вручную

    // 2. @Formula — вычисляется SQL-запросом в БД
    @Formula(value = "width * height")
    private Integer area; // Вычисляется как width * height

    // 3. Ещё один пример @Formula с условием
    @Formula(value = "CASE WHEN width > height THEN 'WIDE' ELSE 'TALL' END")
    private String shapeType;

    public Rectangle(String name, Integer width, Integer height) {
        this.name = name;
        this.width = width;
        this.height = height;
        // Вычисляем perimeter вручную
        this.perimeter = (width + height) * 2;
    }

    // Метод для обновления perimeter (вызываем вручную)
    public void updatePerimeter() {
        this.perimeter = (width + height) * 2;
    }
}