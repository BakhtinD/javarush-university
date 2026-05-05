package com.javarush.controller;

import com.javarush.entity.User;
import com.javarush.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * AdminController — эндпоинты только для ADMIN.
 *
 * Доступ ограничен на уровне URL: .requestMatchers("/admin/**").hasRole("ADMIN")
 * hasRole("ADMIN") — это сокращение для hasAuthority("ROLE_ADMIN").
 *
 * Если пользователь без роли ADMIN попытается обратиться к /admin/**,
 * Spring Security вернет 403 Forbidden.
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/dashboard")
    public Map<String, String> dashboard(Authentication authentication) {
        return Map.of(
            "message", "Добро пожаловать в панель администратора!",
            "currentAdmin", authentication.getName(),
            "info", "Этот эндпоинт доступен только пользователям с ролью ROLE_ADMIN"
        );
    }

    /**
     * Список всех пользователей.
     * Доступ защищён как на уровне URL (/admin/**), так и через @PreAuthorize в UserService.
     */
    @GetMapping("/users")
    public List<Map<String, String>> listUsers() {
        return userService.getAllUsers().stream()
            .map(user -> Map.of(
                "id", String.valueOf(user.getId()),
                "username", user.getUsername(),
                "role", user.getRole()
            ))
            .toList();
    }

    /**
     * Демонстрирует @PreAuthorize с параметром метода (#username).
     * UserService.getUserByUsername() защищён аннотацией:
     * @PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
     */
    @GetMapping("/users/{username}")
    public Map<String, String> getUser(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        return Map.of(
            "username", user.getUsername(),
            "role", user.getRole()
        );
    }
}
