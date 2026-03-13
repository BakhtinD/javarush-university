package com.javarush.controller;

import com.javarush.domain.User;
import com.javarush.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable long id) {
        return userService.findUserById(id);
    }

    @PostMapping
    public void updateUser(@RequestBody User user) {
        userService.updateUser(user);
    }

}
