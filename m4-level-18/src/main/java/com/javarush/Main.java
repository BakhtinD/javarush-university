package com.javarush;

import com.javarush.util.HibernateUtil;
import com.javarush.entity.Account;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class Main {
    public static void main(String[] args) {
        demonstrateTransactionIsolation();
        HibernateUtil.shutdown();
    }

    public static void demonstrateTransactionIsolation() {
        System.out.println("=== Демонстрация установки уровня изоляции транзакции ===");

        Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            // 1. Начинаем транзакцию
            Transaction tx = session.beginTransaction();

            // 2. ЯВНО УСТАНАВЛИВАЕМ УРОВЕНЬ ИЗОЛЯЦИИ для этой транзакции
            // Например, REPEATABLE READ (очень распространённый уровень)
            System.out.println("Устанавливаем уровень изоляции: REPEATABLE READ");
            session.createNativeQuery("SET TRANSACTION ISOLATION LEVEL REPEATABLE READ")
                    .executeUpdate();

            // 3. Выполняем бизнес-логику в рамках этой транзакции
            // Например, находим счёт и изменяем его баланс
            Account account = session.get(Account.class, 1L);
            if (account != null) {
                System.out.printf("Найден счёт: %s, баланс: %.2f%n",
                        account.getAccountNumber(), account.getBalance());
                account.setBalance(account.getBalance() + 100.0);
                session.update(account);
                System.out.println("Баланс увеличен на 100.");
            } else {
                System.out.println("Счёт не найден, создаём новый...");
                Account newAccount = new Account();
                newAccount.setAccountNumber("ACC-" + System.currentTimeMillis());
                newAccount.setBalance(500.0);
                session.save(newAccount);
            }

            // 4. Фиксируем изменения
            tx.commit();
            System.out.println("Транзакция успешно завершена с уровнем REPEATABLE READ.");

        } catch (Exception e) {
            System.err.println("Ошибка в транзакции: " + e.getMessage());
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
                System.out.println("Транзакция откачена.");
            }
        } finally {
            session.close();
        }
    }
}