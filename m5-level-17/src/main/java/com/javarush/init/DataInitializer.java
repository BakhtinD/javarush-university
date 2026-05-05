package com.javarush.init;

import com.javarush.entity.User;
import com.javarush.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * DataInitializer — инициализация тестовых пользователей при старте приложения.
 *
 * Пароли кодируются через BCryptPasswordEncoder.
 * Это демонстрирует, как должна выглядеть безопасная регистрация пользователей.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.count() == 0) {
            // Создаём администратора
            User admin = new User("admin", passwordEncoder.encode("admin123"), "ROLE_ADMIN");
            userRepository.save(admin);

            // Создаём обычного пользователя
            User user = new User("user", passwordEncoder.encode("user123"), "ROLE_USER");
            userRepository.save(user);

            log.info("Тестовые пользователи созданы: admin/admin123 (ROLE_ADMIN), user/user123 (ROLE_USER)");
        }
    }
}
