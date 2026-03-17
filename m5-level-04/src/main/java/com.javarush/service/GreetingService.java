package com.javarush.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "feature.greeting.enabled", havingValue = "true", matchIfMissing = false)
public class GreetingService {

    public String GetGreeting() {
        return "Hello from Conditional Service!";
    }

}
