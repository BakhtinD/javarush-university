

1. Структура JAR:
```angular2html
my-app.jar
  META-INF/
    MANIFEST.MF <- метаинформация (главный класс, версия, зависимости) 
  com/
    javarush/
       Main.class 
  application.properties <- ресурсы
```

2. Структура WAR:
```angular2html
my-webapp.war
  META-INF/
     MANIFEST.MF <- метаинформация (версия, зависимости)
  WEB-INF/ <- закрытая часть
     web.xml  <- дескриптор развертывания
     classes/
        com/javarush
             MyServlet.class 
     lib/
     applicationContext.xml 
  (публичная часть)
     index.html <- главный файл web-страницы
     style.css 
     script.js 
     images/
        logo.png (разные картинки)
```

