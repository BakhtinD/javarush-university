<%--
  Created by IntelliJ IDEA.
  User: sergeyproshchaev
  Date: 30.03.2026
  Time: 21:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>

<jsp:useBean id="product" class="com.javarush.beans.ProductBean" scope="session"/>

  <jsp:setProperty name="product" property="name" value="телефон"/>
  <jsp:setProperty name="product" property="price" value="1000"/>
  <jsp:setProperty name="product" property="quantity" value="5"/>

  <h2>Продукт:</h2>
    <p>
      Название:
      <jsp:getProperty name="product" property="name"/>
    </p>

</body>
</html>