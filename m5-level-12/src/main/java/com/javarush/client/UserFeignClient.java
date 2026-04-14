package com.javarush.client;

import com.javarush.dto.UserDto;
import feign.Param;
import feign.RequestLine;

// Декларативный HTTP-клиент на основе raw OpenFeign (без Spring Cloud).
// Реализацию интерфейса генерирует Feign.builder() в AppConfig.
public interface UserFeignClient {

    @RequestLine("GET /users/{id}")
    UserDto getUserById(@Param("id") Long id);

}
