package com.javarush.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * PublicController — эндпоинты без аутентификации (permitAll в SecurityConfig).
 *
 * Путь /public/** открыт для всех: .requestMatchers("/public/**").permitAll()
 */
@RestController
@RequestMapping("/public")
public class PublicController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello! Это публичный эндпоинт — аутентификация не требуется.";
    }

    @GetMapping("/info")
    public Map<String, String> info() {
        return Map.of(
            "app", "Spring Security Demo",
            "level", "17",
            "topic", "Аутентификация, авторизация, BCrypt, роли"
        );
    }
}
