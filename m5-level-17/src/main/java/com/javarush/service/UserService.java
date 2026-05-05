package com.javarush.service;

import com.javarush.entity.User;
import com.javarush.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Регистрация нового пользователя.
     * Пароль кодируется через BCrypt перед сохранением в БД.
     */
    public User registerUser(String username, String rawPassword) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Пользователь уже существует: " + username);
        }
        String hashedPassword = passwordEncoder.encode(rawPassword);
        return userRepository.save(new User(username, hashedPassword, "ROLE_USER"));
    }

    /**
     * Метод-уровневая безопасность через @PreAuthorize.
     *
     * Только ADMIN может получить список всех пользователей.
     * Аннотация проверяется ДО вызова метода (Pre = before).
     * Требует @EnableMethodSecurity в SecurityConfig.
     */
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Пример сложного правила @PreAuthorize с использованием SpEL.
     *
     * Метод доступен если пользователь — администратор
     * ИЛИ если username совпадает с именем текущего аутентифицированного пользователя.
     * #username — это аргумент метода, доступный в SpEL.
     * authentication.name — имя текущего пользователя из SecurityContext.
     */
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + username));
    }
}
