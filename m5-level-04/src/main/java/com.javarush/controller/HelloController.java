package com.javarush.controller;

import com.javarush.service.GreetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired(required = false)
    private GreetingService greetingService;

    @GetMapping("/")
    public String sayHello() {
        return "Hello World!";
    }

    @GetMapping("/greeting")
    public String greeting() {
        if (greetingService != null) {
            return greetingService.GetGreeting();
        } else {
            return "greetingService is disabled";
        }
    }



}
