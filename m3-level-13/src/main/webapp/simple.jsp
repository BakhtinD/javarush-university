<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<body>
<h2>Самая простая JSP</h2>
<p>Сейчас: <%= new java.util.Date() %></p>
<%
    String user = request.getParameter("name");
    if (user == null) user = "незнакомец";
%>
<p>Привет, <%= user %>!</p>
</body>
</html>