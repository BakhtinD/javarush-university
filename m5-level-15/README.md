# Модуль 5. Уровень 15: Введение в GraphQL

Пример демонстрирует ключевые концепции **Spring for GraphQL**:
- Определение схемы на SDL (Schema Definition Language)
- **Query** — запросы на чтение данных
- **Mutation** — создание и удаление данных
- **@QueryMapping / @MutationMapping** — связывание схемы с Java-методами
- **@SchemaMapping** — Data Fetcher для вложенных полей (nested types)
- Проблема **N+1** и где она возникает при работе с GraphQL

База данных: **H2 in-memory** (авторы и книги).

---

## Структура проекта

```
m5-level-15/
├── src/main/java/com/javarush/
│   ├── Main.java
│   ├── controller/
│   │   ├── AuthorController.java   # @QueryMapping, @MutationMapping для Author
│   │   └── BookController.java     # @QueryMapping, @MutationMapping, @SchemaMapping
│   ├── entity/
│   │   ├── Author.java
│   │   └── Book.java
│   ├── repository/
│   │   ├── AuthorRepository.java
│   │   └── BookRepository.java
│   └── service/
│       ├── AuthorService.java
│       └── BookService.java
└── src/main/resources/
    ├── graphql/
    │   └── schema.graphqls         # GraphQL-схема (SDL)
    ├── application.properties
    ├── data.sql                    # Начальные данные
    └── schema.sql                  # DDL таблиц
```

---

## Запуск

```bash
cd m5-level-15
mvn spring-boot:run
```

---

## Эндпоинты

| URL | Описание |
|-----|----------|
| `http://localhost:8080/graphql` | GraphQL API (POST) |
| `http://localhost:8080/graphiql` | GraphiQL — браузерная IDE для запросов |
| `http://localhost:8080/h2-console` | Консоль H2 (JDBC URL: `jdbc:h2:mem:testdb`) |

---

## Схема GraphQL

Схема находится в `src/main/resources/graphql/schema.graphqls`.
Spring for GraphQL автоматически подхватывает все файлы `*.graphqls` из этой директории.

```graphql
type Query {
    allBooks: [Book!]!
    bookById(id: ID!): Book
    allAuthors: [Author!]!
    authorById(id: ID!): Author
}

type Mutation {
    addAuthor(name: String!, bio: String): Author
    addBook(title: String!, authorId: ID!): Book
    deleteBook(id: ID!): Boolean
}

type Book {
    id: ID!
    title: String!
    author: Author
}

type Author {
    id: ID!
    name: String!
    bio: String
    books: [Book!]!
}
```

---

## CURL команды

GraphQL использует **один эндпоинт** `/graphql` для всех операций (POST).
Клиент сам определяет, какие поля ему нужны — принцип "no over-fetching".

### Queries (чтение данных)

Получить все книги (только название и имя автора — без лишних полей):
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"{ allBooks { id title author { name } } }"}'
```

Получить книгу по ID:
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"{ bookById(id: \"1\") { id title author { id name bio } } }"}'
```

Получить всех авторов (без книг — только имена):
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"{ allAuthors { id name } }"}'
```

Получить автора со всеми его книгами (nested query):
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"{ authorById(id: \"1\") { id name bio books { id title } } }"}'
```

Получить всех авторов с книгами — здесь срабатывает `@SchemaMapping` для поля `books`:
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"{ allAuthors { id name books { title } } }"}'
```

Именованный запрос с операцией `query`:
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"query GetBookWithAuthor { bookById(id: \"2\") { title author { name } } }"}'
```

### Mutations (изменение данных)

Добавить нового автора:
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"mutation { addAuthor(name: \"Иван Тургенев\", bio: \"Русский писатель\") { id name bio } }"}'
```

Добавить книгу (authorId — ID существующего автора):
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"mutation { addBook(title: \"Отцы и дети\", authorId: \"4\") { id title author { name } } }"}'
```

Удалить книгу:
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"mutation { deleteBook(id: \"7\") }"}'
```

### Пример ответа с ошибкой

GraphQL возвращает HTTP **200 OK** даже при ошибках. Информация об ошибке — в поле `errors`:
```bash
curl -X POST http://localhost:8080/graphql \
  -H "Content-Type: application/json" \
  -d '{"query":"mutation { addBook(title: \"Тест\", authorId: \"999\") { id title } }"}'
```

Ответ:
```json
{
  "data": {
    "addBook": null
  },
  "errors": [
    {
      "message": "Author not found: 999",
      "path": ["addBook"],
      "extensions": { "classification": "INTERNAL_ERROR" }
    }
  ]
}
```

---

## Ключевые концепции в коде

### Схема → Java-контроллер

| Схема SDL | Java-аннотация | Метод |
|-----------|----------------|-------|
| `type Query { allBooks }` | `@QueryMapping` | `allBooks()` |
| `type Query { bookById(id: ID!) }` | `@QueryMapping` | `bookById(@Argument Long id)` |
| `type Mutation { addBook(...) }` | `@MutationMapping` | `addBook(@Argument ...)` |
| `type Author { books: [Book] }` | `@SchemaMapping` | `booksForAuthor(Author author)` |

### @SchemaMapping и проблема N+1

`BookController.booksForAuthor()` — это **Data Fetcher** для поля `Author.books`.
GraphQL-движок вызывает его **для каждого** объекта `Author` в результате:
- 1 запрос на `allAuthors` → N авторов
- N вызовов `booksForAuthor` → N SQL-запросов к таблице BOOKS

Итого: **1 + N запросов** к БД — проблема N+1.  
Решение — **DataLoader** (batching): объединяет N запросов в один `SELECT * FROM BOOKS WHERE author_id IN (1, 2, ..., N)`.

---

## References

1. [Spring for GraphQL — официальная документация](https://docs.spring.io/spring-graphql/reference/)
2. [GraphQL — спецификация](https://spec.graphql.org/)
3. [GraphQL Java](https://www.graphql-java.com/)
