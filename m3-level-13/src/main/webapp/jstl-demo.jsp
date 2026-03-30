<%--
  Created by IntelliJ IDEA.
  User: sergeyproshchaev
  Date: 30.03.2026
  Time: 21:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>

<body>
<h2>Минимальный JSTL</h2>
<%-- Создаем список --%>
<c:set var="names">
    Иван,Мария
</c:set>

<ul>
  <c:forEach items="${names}" var="name">
    <li>${name}</li>
  </c:forEach>
</ul>

</body>
</html>
