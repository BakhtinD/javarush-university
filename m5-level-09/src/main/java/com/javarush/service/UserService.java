package com.javarush.service;

import com.javarush.dto.UserDto;
import com.javarush.entity.User;
import com.javarush.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // User by ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // All Users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<UserDto> findUserDtoById(Long id) {
        return userRepository.findById(id)
                .map(user -> new UserDto(user.getId(),
                        user.getName(),
                        user.getEmail()));
    }

    // todo - дописать остальные методы для сущности USER и заменить использование репозитория
    // в контроллере на сервисный слой


}
