package com.javarush.controller;

import com.javarush.entity.User;
import com.javarush.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class HelloController {

    private final UserRepository userRepository;

    @Autowired
    public HelloController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String sayHello() {
        return "Hello World!";
    }

    @GetMapping("/users")
    public List<User> getUsers() {

        return userRepository.findAll();

//        return Arrays.asList(
//                new User(1L, "Alice", "alice@example.com"),
//                new User(2L,"Bob", "bob@example.com"),
//                new User(3L,"Charlie", "charlie@example.com")
//        );
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("User not found: " + id));
    }

    // GET /users/email?email=alice@example.com
    @GetMapping("/users/email")
    public User getUserByEmail(@RequestParam String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("User not found with email: " + email));
    }

}
