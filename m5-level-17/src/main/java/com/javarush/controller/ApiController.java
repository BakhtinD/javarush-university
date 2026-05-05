package com.javarush.controller;

import com.javarush.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * ApiController — демонстрация метод-уровневой безопасности (@PreAuthorize).
 *
 * Все /api/** эндпоинты требуют аутентификации (см. SecurityConfig).
 * Тонкое управление доступом реализуется через @PreAuthorize на уровне метода,
 * что позволяет использовать сложную бизнес-логику в правилах доступа.
 *
 * @PreAuthorize использует SpEL (Spring Expression Language):
 * - hasRole('ADMIN') — проверяет наличие роли ROLE_ADMIN
 * - hasAnyRole('USER', 'ADMIN') — любая из ролей
 * - isAuthenticated() — любой аутентифицированный пользователь
 * - authentication.name — имя текущего пользователя
 * - #paramName — значение аргумента метода
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final UserService userService;

    /**
     * Доступен любому аутентифицированному пользователю.
     * @PreAuthorize("isAuthenticated()") — эквивалент .anyRequest().authenticated()
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public Map<String, String> me(Authentication authentication) {
        return Map.of(
            "username", authentication.getName(),
            "message", "Это ваша информация. Доступна любому аутентифицированному пользователю."
        );
    }

    /**
     * Доступен только ADMIN — на уровне метода, через @PreAuthorize.
     * Демонстрирует разницу: URL /api/** открыт для всех аутентифицированных,
     * но конкретный метод ограничен только ADMIN.
     */
    @GetMapping("/admin-only")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> adminOnly(Authentication authentication) {
        return Map.of(
            "message", "Этот метод защищён @PreAuthorize('hasRole(ADMIN)').",
            "currentUser", authentication.getName()
        );
    }

    /**
     * Пример сложного SpEL-правила: ADMIN может смотреть данные любого пользователя,
     * обычный пользователь может смотреть только свои данные.
     *
     * #username — значение аргумента метода (связывание по имени параметра).
     * authentication.name — имя текущего аутентифицированного пользователя.
     */
    @GetMapping("/users/{username}")
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
    public Map<String, String> getUserInfo(@PathVariable String username) {
        var user = userService.getUserByUsername(username);
        return Map.of(
            "username", user.getUsername(),
            "role", user.getRole(),
            "note", "ADMIN может смотреть данные любого; USER — только свои"
        );
    }

    /**
     * Демонстрация hasAuthority vs hasRole.
     * hasRole("ADMIN") == hasAuthority("ROLE_ADMIN") — это одно и то же!
     * Spring автоматически добавляет префикс ROLE_ при использовании hasRole().
     */
    @GetMapping("/role-vs-authority")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Map<String, String> roleVsAuthority() {
        return Map.of(
            "info", "hasRole('ADMIN') эквивалентно hasAuthority('ROLE_ADMIN')",
            "explanation", "Spring Security автоматически добавляет префикс ROLE_ при hasRole()"
        );
    }
}
