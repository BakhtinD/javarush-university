package com.javarush.quest;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/start")
public class StartServlet extends HttpServlet {

    // "/index.jsp"
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/index.jsp").forward(req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String playerName = req.getParameter("playerName");
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Искатель приключений";
        }

        HttpSession session = req.getSession();
        session.setAttribute("playerName", playerName);
        session.setAttribute("gamesPlayed", 0);

        resp.sendRedirect(req.getContextPath() + "/game?step=start");
    }

}
