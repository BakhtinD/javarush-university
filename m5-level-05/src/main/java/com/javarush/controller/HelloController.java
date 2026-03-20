package com.javarush.controller;

import com.javarush.entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class HelloController {

    @GetMapping("/")
    public String sayHello() {
        return "Hello World!";
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return Arrays.asList(
                new User(1L, "Alice", "alice@example.com"),
                new User(2L,"Bob", "bob@example.com"),
                new User(3L,"Charlie", "charlie@example.com")
        );
    }

}
