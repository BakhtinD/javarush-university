package com.javarush.service;

import com.javarush.entity.User;
import com.javarush.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * CustomUserDetailsService — реализация UserDetailsService для загрузки пользователей из БД.
 *
 * Spring Security вызывает loadUserByUsername() в процессе аутентификации:
 * 1. Пользователь вводит логин и пароль в форму входа.
 * 2. AuthenticationManager вызывает этот метод, передавая введённый логин.
 * 3. Мы находим пользователя в БД и возвращаем объект UserDetails с хэшем пароля.
 * 4. AuthenticationManager сравнивает введённый пароль с хэшем через PasswordEncoder.matches().
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));

        // Преобразуем строку роли (напр. "ROLE_ADMIN") в объект GrantedAuthority
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword()) // BCrypt-хэш из БД
            .authorities(List.of(new SimpleGrantedAuthority(user.getRole())))
            .build();
    }
}
