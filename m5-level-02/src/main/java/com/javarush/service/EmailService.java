package com.javarush.service;

import com.javarush.model.User;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void sendEmail(User user, String message) {
        System.out.println("Sending email...");
    }

}
