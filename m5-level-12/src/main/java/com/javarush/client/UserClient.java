package com.javarush.client;

import com.javarush.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserClient {

    private final RestTemplate restTemplate;
    private static final String BASE_URL = "http://localhost:8080/users";


    public Optional<UserDto> getUserById(Long id) {
        String url = BASE_URL + "/" + id;
        return Optional.ofNullable(restTemplate.getForObject(url, UserDto.class));
    }

}
