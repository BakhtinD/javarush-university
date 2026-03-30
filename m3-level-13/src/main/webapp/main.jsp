<%--
  Created by IntelliJ IDEA.
  User: sergeyproshchaev
  Date: 30.03.2026
  Time: 21:19
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<body>
<header>
  <h1>Мой сайт</h1>
  <nav>        <a href="/">Главная</a>
    <a href="/about">О нас</a>
    <a href="/contact">Контакты</a>
  </nav></header>

</header>

<h2>Примеры переадресации</h2>
<p><a href="contacts.jsp">Перейти на контакты (обычная ссылка)</a></p>

<!-- Редирект -->
<%
  if (request.getParameter("action") != null &&
  request.getParameter("action").equals("redirect")) {
    response.sendRedirect("contacts.jsp");
    return;
  }
%>

<!-- Forward -->
<%
  if (request.getParameter("action") != null &&
          request.getParameter("action").equals("forward")) {
%>
<jsp:forward page="contacts.jsp"/>
<%
  }
%>
<p><a href="main.jsp?action=forward">Forward на контакты (jsp:forward)</a></p>

</body>
</html>
