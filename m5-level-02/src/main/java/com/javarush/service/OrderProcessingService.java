package com.javarush.service;

import com.javarush.model.User;
import com.javarush.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderProcessingService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentService paymentService;

    public void processOrder(User user) {
        // логика создания заказа для User
    }

}
