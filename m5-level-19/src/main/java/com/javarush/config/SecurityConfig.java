package com.javarush.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Конфигурация безопасности Actuator.
 *
 * Правила доступа:
 * - /actuator/health и /actuator/info — открыты для всех (для readiness/liveness probe).
 * - /actuator/**                       — только пользователь с ролью ADMIN.
 * - Всё остальное                      — открыто для всех (бизнес-эндпоинты).
 *
 * Учётные данные: admin / admin
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                .requestMatchers("/h2-console/**").permitAll()
                .anyRequest().permitAll()
            )
            .httpBasic(Customizer.withDefaults())
            // Отключаем CSRF для H2-консоли и Actuator (только для учебного примера!)
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**", "/actuator/**", "/**"))
            // Разрешаем фреймы для H2-консоли
            .headers(headers -> headers.frameOptions(fo -> fo.sameOrigin()));

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // {noop} — пароль в открытом виде (только для учебного примера!)
        UserDetails admin = User.builder()
                .username("admin")
                .password("{noop}admin")
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(admin);
    }
}
