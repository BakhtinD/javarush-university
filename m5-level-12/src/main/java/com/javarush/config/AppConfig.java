package com.javarush.config;

import com.javarush.client.UserFeignClient;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // Feign.builder() создаёт реализацию интерфейса UserFeignClient на основе аннотаций @RequestLine
    @Bean
    public UserFeignClient userFeignClient(@Value("${app.feign.base-url:http://localhost:8080}") String baseUrl) {
        return Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(UserFeignClient.class, baseUrl);
    }

}
