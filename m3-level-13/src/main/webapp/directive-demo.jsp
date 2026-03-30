<%--
  Created by IntelliJ IDEA.
  User: sergeyproshchaev
  Date: 30.03.2026
  Time: 20:28
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ page import="java.util.Date, java.text.SimpleDateFormat" %>
<%@ page session="true" buffer="16kb" autoFlush="true" %>
<%@ page isELIgnored="false" isThreadSafe="true" %>
<%@ page errorPage="/WEB-INF/error.jsp" %>
<%@ page info="Учебный пример JSP от JavaRush" %>
<!DOCTYPE html>
<html>
<head>
    <title>Пример директив JSP</title>
</head>
<body>
   <h1>Информация о странице:</h1>
   <p>Servlet Info <%= getServletInfo() %></p>
   <p>Дата: <%= new Date() %></p>
</body>
</html>
