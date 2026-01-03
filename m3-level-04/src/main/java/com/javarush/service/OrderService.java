package com.javarush.service;

import com.javarush.model.Order;
import com.javarush.repository.DatabaseRepository;

public class OrderService {
    private DatabaseRepository repository;
    private PaymentGateway paymentGateway;

    public OrderService(DatabaseRepository repository,
                        PaymentGateway paymentGateway) {
        this.repository = repository;
        this.paymentGateway = paymentGateway;
    }

    public boolean processOrder(Order order) {
        repository.save(order);
        boolean paymentSuccess = paymentGateway.processPayment(
                order.getAmount(),
                order.getCardNumber()
        );

        if (!paymentSuccess) {
            repository.delete(order.getId());
        }

        return paymentSuccess;
    }
}
