package com.javarush.controller;

import com.javarush.entity.User;
import com.javarush.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * AuthController — регистрация новых пользователей.
 *
 * POST /register открыт без аутентификации: .requestMatchers("/register").permitAll()
 * CSRF отключён для /register для удобства тестирования через curl.
 */
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * Регистрация нового пользователя.
     *
     * Пример запроса:
     * POST /register
     * {"username": "newuser", "password": "secret123"}
     *
     * Пароль кодируется через BCryptPasswordEncoder перед сохранением в БД.
     * Новый пользователь получает роль ROLE_USER.
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.registerUser(request.username(), request.password());
            return ResponseEntity.ok(Map.of(
                "message", "Пользователь успешно зарегистрирован",
                "username", user.getUsername(),
                "role", user.getRole()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    record RegisterRequest(String username, String password) {}
}
