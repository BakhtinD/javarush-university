package com.javarush.example;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data // @Getter + @Setter + @ToString + @EqualsAndHashCode +
@AllArgsConstructor
@Builder
class Human {
    private String name;
    private int age;
    private String email;
}

public class LombokDemo {
    public static void main(String[] args) {
        Human human = new Human("Name", 10, "email");
        human.setName("Name!");
        System.out.println(human.toString());

        //todo Написать пример с использованием билдера

    }
}
