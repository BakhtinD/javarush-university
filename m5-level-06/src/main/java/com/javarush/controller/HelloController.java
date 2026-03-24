package com.javarush.controller;

import com.javarush.entity.User;
import com.javarush.repository.UserRepository;
import com.javarush.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor // вместо конструктора с внедрением зависимостей
// @AllArgsConstructor
public class HelloController {

    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("/")
    public String sayHello() {
        return "Hello World!";
    }

    @PostMapping("/users/update-emails")
    public ResponseEntity<String> updateEmails(
            @RequestParam Long id1,
            @RequestParam Long id2,
            @RequestParam String email1,
            @RequestParam String email2) {

        userService.updateUserEmails(id1, id2, email1, email2);
        return ResponseEntity.ok("Emails updated successfully");
    }


}
