# m5-level-17: Spring Security — основы безопасности

Демонстрационный проект по материалам вебинара **Level 17**.

Темы: аутентификация, авторизация, SecurityFilterChain, UserDetailsService, BCrypt, роли и полномочия, метод-уровневая безопасность (@PreAuthorize).

---

## Запуск

```bash
mvn spring-boot:run -pl m5-level-17
```

Или запустить `Main.java` через IDE.

При старте автоматически создаются два тестовых пользователя:

| Пользователь | Пароль     | Роль        |
|-------------|-----------|-------------|
| `admin`     | `admin123` | `ROLE_ADMIN` |
| `user`      | `user123`  | `ROLE_USER`  |

---

## Структура проекта

```
src/main/java/com/javarush/
├── Main.java                          — точка входа Spring Boot
├── config/
│   └── SecurityConfig.java            — конфигурация безопасности (SecurityFilterChain, PasswordEncoder)
├── entity/
│   └── User.java                      — JPA-сущность пользователя
├── repository/
│   └── UserRepository.java            — Spring Data JPA репозиторий
├── service/
│   ├── CustomUserDetailsService.java  — загрузка пользователя из БД для Spring Security
│   └── UserService.java               — бизнес-логика с @PreAuthorize
├── controller/
│   ├── PublicController.java          — /public/** (без аутентификации)
│   ├── UserController.java            — /user/** (USER или ADMIN)
│   ├── AdminController.java           — /admin/** (только ADMIN)
│   ├── AuthController.java            — /register (регистрация)
│   └── ApiController.java             — /api/** (метод-уровневая безопасность)
└── init/
    └── DataInitializer.java           — создание тестовых пользователей при старте
```

---

## Правила доступа (SecurityFilterChain)

| URL-паттерн          | Кто может обращаться            | Метод в SecurityConfig              |
|----------------------|---------------------------------|-------------------------------------|
| `/public/**`         | Все (без аутентификации)        | `.permitAll()`                      |
| `/register`          | Все (без аутентификации)        | `.permitAll()`                      |
| `/login`             | Все (без аутентификации)        | `.permitAll()`                      |
| `/h2-console/**`     | Все (для разработки)            | `.permitAll()`                      |
| `/admin/**`          | Только `ROLE_ADMIN`             | `.hasRole("ADMIN")`                 |
| `/user/**`           | `ROLE_USER` или `ROLE_ADMIN`    | `.hasAnyRole("USER", "ADMIN")`      |
| `/api/**`            | Любой аутентифицированный       | `.authenticated()` + @PreAuthorize  |
| Всё остальное        | Любой аутентифицированный       | `.anyRequest().authenticated()`     |

---

## Эндпоинты

### Публичные (без аутентификации)

```bash
# Публичное приветствие
curl http://localhost:8080/public/hello

# Информация о приложении
curl http://localhost:8080/public/info
```

### Регистрация нового пользователя

```bash
curl -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{"username": "newuser", "password": "secret123"}'
```

Пароль кодируется через BCrypt перед сохранением. Новому пользователю присваивается роль `ROLE_USER`.

### Эндпоинты для пользователей (USER и ADMIN)

Используйте `-u username:password` для HTTP Basic Auth:

```bash
# Профиль текущего пользователя — показывает имя и роли
curl -u user:user123 http://localhost:8080/user/profile

# Пользовательский дашборд
curl -u user:user123 http://localhost:8080/user/dashboard

# Попытка войти с неправильным паролем — вернёт 401 Unauthorized
curl -u user:wrongpassword http://localhost:8080/user/profile
```

### Эндпоинты для администраторов (только ADMIN)

```bash
# Панель администратора
curl -u admin:admin123 http://localhost:8080/admin/dashboard

# Список всех пользователей в системе
curl -u admin:admin123 http://localhost:8080/admin/users

# Данные конкретного пользователя (только для ADMIN)
curl -u admin:admin123 http://localhost:8080/admin/users/user

# Попытка обычного пользователя зайти в /admin/** — вернёт 403 Forbidden
curl -u user:user123 http://localhost:8080/admin/dashboard
```

### API с метод-уровневой безопасностью (@PreAuthorize)

```bash
# Информация о себе — доступна любому аутентифицированному
curl -u user:user123 http://localhost:8080/api/me
curl -u admin:admin123 http://localhost:8080/api/me

# Только для ADMIN (защищено @PreAuthorize("hasRole('ADMIN')") на методе)
curl -u admin:admin123 http://localhost:8080/api/admin-only
# Вернёт 403 для обычного пользователя:
curl -u user:user123 http://localhost:8080/api/admin-only

# Данные пользователя: ADMIN видит любого, USER — только себя
# (SpEL: @PreAuthorize("hasRole('ADMIN') or #username == authentication.name"))
curl -u admin:admin123 http://localhost:8080/api/users/user    # OK — admin смотрит user
curl -u user:user123 http://localhost:8080/api/users/user      # OK — user смотрит себя
curl -u user:user123 http://localhost:8080/api/users/admin     # 403 — user не может смотреть admin

# Демонстрация hasRole vs hasAuthority — они эквивалентны!
curl -u admin:admin123 http://localhost:8080/api/role-vs-authority
```

---

## H2 Console (просмотр базы данных)

Откройте в браузере: **http://localhost:8080/h2-console/**

Параметры подключения:
- **JDBC URL:** `jdbc:h2:mem:testdb`
- **User Name:** `sa`
- **Password:** *(пустой)*

Полезные SQL-запросы:
```sql
-- Посмотреть всех пользователей (пароли хранятся в виде BCrypt-хэша)
SELECT * FROM USERS;
```

---

## Форма входа (браузер)

Spring Security автоматически предоставляет стандартную форму входа:

**http://localhost:8080/login**

После входа в браузере сессия сохраняется через cookie `JSESSIONID` — это **stateful**-подход.

---

## Ключевые концепции из материала

### Аутентификация vs Авторизация

- **Аутентификация** — "Кто ты?" Проверяется логин и пароль.
- **Авторизация** — "Что тебе можно?" Проверяется после успешной аутентификации.

### Цепочка фильтров (Security Filter Chain)

Каждый HTTP-запрос проходит через цепочку фильтров Spring Security перед тем, как попасть в контроллер. Фильтры проверяют CSRF, аутентификацию, авторизацию.

### BCrypt и хранение паролей

Пароли хранятся в виде BCrypt-хэша. BCrypt:
- добавляет случайную «соль» (salt) — одинаковые пароли дают разные хэши
- настраиваемая «сложность» (work factor) — замедляет brute-force атаки
- **необратим** — Spring Security не расшифровывает пароль, а хэширует введённый и сравнивает

### UserDetailsService

Интерфейс с одним методом `loadUserByUsername(String username)`. Реализация (`CustomUserDetailsService`) загружает пользователя из БД и возвращает объект `UserDetails` с хэшем пароля и ролями.

### Роли и полномочия (Roles & Authorities)

- `GrantedAuthority` — единичное право (строка)
- **Роль** — `GrantedAuthority` с префиксом `ROLE_` (соглашение)
- `hasRole("ADMIN")` == `hasAuthority("ROLE_ADMIN")` — эквиваленты

### @PreAuthorize и метод-уровневая безопасность

Требует `@EnableMethodSecurity` в конфигурации. Использует SpEL:

```java
@PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
public User getUserByUsername(String username) { ... }
```

Позволяет выражать сложные правила доступа прямо на методах сервисов.
