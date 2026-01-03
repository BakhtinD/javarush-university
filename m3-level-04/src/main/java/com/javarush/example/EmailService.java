package com.javarush.example;

public interface EmailService {
    boolean sendEmail(String to, String subject, String body);
}