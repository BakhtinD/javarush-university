package com.javarush.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * UserController — эндпоинты для аутентифицированных пользователей.
 *
 * Путь /user/** требует роли USER или ADMIN:
 * .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
 *
 * Spring Security автоматически внедряет объект Authentication в параметры метода.
 * Authentication содержит информацию о текущем пользователе: имя, роли.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/profile")
    public Map<String, Object> profile(Authentication authentication) {
        List<String> roles = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .toList();

        return Map.of(
            "username", authentication.getName(),
            "roles", roles,
            "message", "Вы успешно аутентифицированы! Этот эндпоинт доступен для USER и ADMIN."
        );
    }

    @GetMapping("/dashboard")
    public Map<String, String> dashboard(Authentication authentication) {
        return Map.of(
            "message", "Добро пожаловать в пользовательский раздел, " + authentication.getName() + "!"
        );
    }
}
