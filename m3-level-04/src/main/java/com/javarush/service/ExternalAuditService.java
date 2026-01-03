package com.javarush.service;


public interface ExternalAuditService {
    void logUserCreation(String username, String userId);
    void logUserDeletion(String userId);
}
