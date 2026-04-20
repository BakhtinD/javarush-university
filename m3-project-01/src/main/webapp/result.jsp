<%--
  Created by IntelliJ IDEA.
  User: sergeyproshchaev
  Date: 20.04.2026
  Time: 20:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Результат</title>
</head>
<body>
    <h1>${sessionScope.result}</h1>
    <a href="hello">Начать заново</a>

    <hr>
    <h3>Статистика сессии</h3>
    <p>ID сессии: ${pageContext.session.id}</p>
    <p>Время создания: ${pageContext.session.creationTime}</p>

</body>
</html>
