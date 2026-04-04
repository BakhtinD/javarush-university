package com.javarush.service;

import com.javarush.entity.User;
import com.javarush.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    // getUserById
    @Test
    @DisplayName("Должен вернуть пользователя, если он есть")
    void getUserById_whenUserExists_shouldReturnUser() {

        // Arrange
        User expectedUser = new User(1L,
                "John Doe"
                , "john@example.com");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(expectedUser));

        // Act
        Optional<User> actualUser = userService.getUserById(1L);

        // Assert
        assertThat(actualUser).isPresent();
        assertThat(actualUser.get().getName()).isEqualTo("John Doe");
    }

}