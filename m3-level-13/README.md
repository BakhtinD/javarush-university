# Уровень 13 JSP JSTL

Запуск Tomcat
```bash
cd m3-level-13 
mvn clean tomcat7:run
```

1. Пример html
```bash
curl http://localhost:8080/index.html
```

2. Пример Servlet
```bash
curl http://localhost:8080/hello
```

3. Пример JSP
```bash
curl "http://localhost:8080/simple.jsp?name=Jon"
```

4. Пример кастомного Тэга
```bash
curl http://localhost:8080/hello-tag-demo.jsp
```