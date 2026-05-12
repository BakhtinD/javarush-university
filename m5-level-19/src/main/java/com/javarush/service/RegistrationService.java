package com.javarush.service;

import com.javarush.entity.User;
import com.javarush.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserRepository userRepository;
    private final PlatformTransactionManager transactionManager;

    private TransactionTemplate transactionTemplate;

    @jakarta.annotation.PostConstruct
    public void init() {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public void registerUser(String name, String email) {
        transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                try {
                    User user = new User(name, email);
                    userRepository.save(user);
                    sendConfirmationEmail(user);
                } catch (Exception e) {
                    status.setRollbackOnly();
                    throw new RuntimeException("Registration failed", e);
                }
                return null;
            }
        });
    }

    private void sendConfirmationEmail(User user) {
        System.out.println("Sending email to " + user.getEmail());
        // Раскомментируйте для демонстрации отката:
        // throw new RuntimeException("Email service unavailable");
    }
}
