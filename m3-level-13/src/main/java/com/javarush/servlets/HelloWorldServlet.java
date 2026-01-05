package com.javarush.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

// Аннотация говорит контейнеру, по какому URL "слушать" этот сервлет
@WebServlet("/hello")
public class HelloWorldServlet extends HttpServlet {

    // Переопределяем ТОЛЬКО метод для обработки GET-запросов
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {

        // Устанавливаем тип контента, который вернем клиенту (браузеру)
        response.setContentType("text/html; charset=UTF-8");

        // Получаем выходной поток для отправки данных клиенту
        PrintWriter out = response.getWriter();

        // Пишем простую HTML-страницу в ответ
        out.println("<html><body>");
        out.println("<h1>Привет, мир от HttpServlet!</h1>");
        out.println("<p>Это ответ на ваш GET-запрос.</p>");
        out.println("<p>Модуль 3. Уровень 13 JSP, JSTL.</p>");
        out.println("</body></html>");
    }

    // Методы doPost, doPut и т.д. не переопределены —
    // запросы этих типов к этому URL будут отклонены с ошибкой 405 (Method Not Allowed).
}