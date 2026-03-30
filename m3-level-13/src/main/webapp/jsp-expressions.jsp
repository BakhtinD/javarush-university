<%--
  Created by IntelliJ IDEA.
  User: sergeyproshchaev
  Date: 30.03.2026
  Time: 20:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Простая JSP</title>
</head>
<body>
    <h1>JSP-страница с Скриплетами и Выражениями</h1>
    <%-- Так пишется JSP-комментарий, который игнорируется JSP контейнером и не попадет в HTML --%>

    <!-- 1) Простой вывод текста -->
    <p> Это обычный HTML-текст </p>

    <!-- 2) Выражение (Expression) -->
    <p> Текущая дата <%= new java.util.Date() %> </p>

    <!-- Простой скриплет -->
    <% String name = "Мир"; %>

    <!-- Использование ранее объявленной переменной из скриплета -->
    <p> Привет, <%= name %>! </p>

    <!-- Простая математика -->
    <p> 5 + 3 = <%= 5 + 3 %> </p>



</body>
</html>
