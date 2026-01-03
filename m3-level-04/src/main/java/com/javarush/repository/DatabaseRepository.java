package com.javarush.repository;

import com.javarush.model.Order;

public interface DatabaseRepository {
    void save(Order order);
    void delete(String orderId);
}
