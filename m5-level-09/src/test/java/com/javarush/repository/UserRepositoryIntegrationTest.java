package com.javarush.repository;

import com.javarush.entity.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
        assertThat(userRepository).isNotNull();
    }

    @Test
    void testSaveAndFindUser() {

        // Save
        User user = new User("Integration Test",
                "integration@example.com");

        User savedUser = userRepository.save(user);

        assertThat(savedUser.getId()).isNotNull();

        // Find
        Optional<User> found = userRepository.findById(savedUser.getId());

        assertThat(found).isPresent();
        // "Integration Test"
        assertThat(found.get().getName()).isEqualTo("Integration Test");
        // "integration@example.com"
        assertThat(found.get().getEmail()).isEqualTo("integration@example.com");

    }

}