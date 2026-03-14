package com.javarush.service;


import com.javarush.domain.User;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public User findUserById(long id) {
        // log.info("Attempting to find user with id: {}", id); // мы перенесли это в аспект

        checkPermissions();

        // Имитация бизнес-логики
        User user = new User(1L, "Alice");

        // log.info("Successfully found user with id: {}", id); // мы перенесли это в аспект
        return user;
    }

    public void updateUser(User user) {

        // log.info("Attempting to update user: {}", user.getName()); // мы перенесли это в аспект

        checkPermissions();

        // Имитация обновления
        log.info("User with id " + user.getId() + " updated");

        // log.info("User with id {} updated"); // мы перенесли это в аспект
    }

    private static void checkPermissions() {
        log.info("Checking permissions...");
    }


}
