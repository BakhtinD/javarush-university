package com.javarush.repository;

import com.javarush.domain.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    public User findById(Long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID должен быть положительным!");
        }
        return new User(1L, "Alice");
    }

}
