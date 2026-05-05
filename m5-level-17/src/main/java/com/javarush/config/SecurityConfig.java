package com.javarush.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Современная конфигурация Spring Security (без устаревшего WebSecurityConfigurerAdapter).
 *
 * @EnableWebSecurity — активирует поддержку веб-безопасности Spring.
 * @EnableMethodSecurity — включает метод-уровневую безопасность (@PreAuthorize и др.).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * SecurityFilterChain — центральный бин конфигурации правил безопасности.
     *
     * Правила authorizeHttpRequests применяются по порядку: от более конкретных к более общим.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Публичные эндпоинты — доступны без аутентификации
                .requestMatchers("/public/**", "/register", "/login").permitAll()
                // H2 Console — разрешаем для разработки
                .requestMatchers("/h2-console/**").permitAll()
                // /admin/** — только для пользователей с ролью ROLE_ADMIN
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // /user/** — для пользователей с любой из перечисленных ролей
                .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                // /api/** — для всех аутентифицированных (тонкий контроль через @PreAuthorize)
                .requestMatchers("/api/**").authenticated()
                // Все остальные запросы требуют аутентификации
                .anyRequest().authenticated()
            )
            // Включаем стандартную форму входа Spring Security (GET /login)
            .formLogin(Customizer.withDefaults())
            // Настройка выхода из системы
            .logout(logout -> logout
                .logoutSuccessUrl("/public/hello")
                .permitAll()
            )
            // Отключаем CSRF для H2 Console и REST API (для удобства тестирования через curl)
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**", "/api/**", "/register")
            )
            // Отключаем X-Frame-Options для работы H2 Console (которая использует фреймы)
            .headers(headers -> headers
                .frameOptions(frame -> frame.disable())
            );

        return http.build();
    }

    /**
     * BCryptPasswordEncoder — рекомендуемый алгоритм хранения паролей.
     *
     * BCrypt добавляет случайную «соль» и настраиваемый «work factor»,
     * что делает перебор паролей (brute-force) крайне медленным.
     * НИКОГДА не храните пароли в открытом виде!
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
