package com.javarush.example;

import java.sql.*;

public class PreparedStatementSimple {

    public static void main(String[] args) throws SQLException {

        // Данные для подключения к БД
        String url = "jdbc:mysql://localhost:3306/sakila";
        String user = "root";       //
        String password = "sakila"; //

        Connection conn = DriverManager.getConnection(url, user, password);

        // 1. Подготовка шаблона с плейсхолдерами
        //                                                                 #1                 #2
        String sql = "SELECT first_name, last_name FROM customer WHERE customer_id = ? AND active = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);

        // 2. Устанавливаем значения
        preparedStatement.setInt(1, 5);        // customer_id = 5 для #1
        preparedStatement.setBoolean(2, true); // active = true для #2

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            System.out.println("Клиент найден: " + resultSet.getString("first_name")
                    + " " + resultSet.getString("last_name"));
        }

        // 3. Повторное использование с другими параметрами
        preparedStatement.setInt(1, 10); // customer_id = 10 для #1
        preparedStatement.setBoolean(2, true); // active = true для #2

        resultSet = preparedStatement.executeQuery(); // Использование кэшируемого плана!

        if (resultSet.next()) {
            System.out.println("Клиент найден: " + resultSet.getString("first_name")
                    + " " + resultSet.getString("last_name"));
        }

        preparedStatement.close();
        conn.close();

    }

}
