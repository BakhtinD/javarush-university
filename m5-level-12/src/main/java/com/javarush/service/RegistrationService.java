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

    // Вместо внедрения TransactionTemplate напрямую, создадим его в методе или конструкторе
    // Но для демонстрации создадим поле
    private TransactionTemplate transactionTemplate;

    // Инициализируем TransactionTemplate после внедрения зависимостей
    @jakarta.annotation.PostConstruct
    public void init() {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public void registerUser(String name, String email) {
        transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                try {
                    // Бизнес-логика: создание пользователя
                    User user = new User(name, email);
                    userRepository.save(user);

                    // Эмулируем отправку письма, которая может выбросить исключение
                    sendConfirmationEmail(user);

                    // Если всё успешно, транзакция зафиксируется автоматически
                } catch (Exception e) {
                    // Вручную помечаем транзакцию на откат
                    status.setRollbackOnly();
                    throw new RuntimeException("Registration failed", e);
                }
                return null;
            }
        });
    }

    private void sendConfirmationEmail(User user) {
        // Имитация отправки письма
        System.out.println("Sending email to " + user.getEmail());

        // Раскомментируйте для демонстрации отката:
        // throw new RuntimeException("Email service unavailable");
    }
}