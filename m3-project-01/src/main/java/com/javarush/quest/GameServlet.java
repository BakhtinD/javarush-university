package com.javarush.quest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * GameServlet - класс, обрабатывающий логику игры.
 * Использует HttpSession для хранения состояния между запросами.
 * Проверяет ввод пользователя, устанавливает результат в сессию.
 * Использует sendRedirect для предотвращения повторной отправки формы.
 */
@WebServlet("/game")
public class GameServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        String answer = req.getParameter("answer");

        if (answer == null) {
            req.setAttribute("message", "Выберите вариант!");
            req.getRequestDispatcher("/question.jsp").forward(req, resp);
            return;
        }

        if ("accept".equals(answer)) {
            session.setAttribute("result", "Победа! Вы приняли вызов.");
        } else {
            session.setAttribute("result", "Поражение! Вы отказались.");
        }

        resp.sendRedirect(req.getContextPath() + "/result.jsp");
    }

}
