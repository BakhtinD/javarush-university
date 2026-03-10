package com.javarush.service;

import com.javarush.model.User;
import com.javarush.repository.InMemoryUserRepository;
import com.javarush.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/***
 * Пример внедрения зависимостей через конструктор
 */
@Service
public class UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;

    // @Autowired
    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

// Для Legacy
//    public UserService() {
//        // Жесткая связь
//        this.userRepository = new InMemoryUserRepository();
//    }

    public User registerUser(String name) {
        User user = new User(null, name);
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

}
