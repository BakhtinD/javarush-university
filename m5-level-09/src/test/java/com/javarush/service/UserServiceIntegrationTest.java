package com.javarush.service;

import com.javarush.entity.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void contextLoads() {
        assertThat(userService).isNotNull();
    }

    @Test
    void getUserById_shouldWorkWithRealDatabase() {
        // INSERT INTO USERS (name, email) VALUES ('Alice', 'alice@example.com');
        Optional<User> user = userService.getUserById(1L);
        assertThat(user).isPresent();
        assertThat(user.get().getName()).isEqualTo("Alice");
        assertThat(user.get().getName()).isIn("Alice", "Bob", "Charlie");
    }

}
