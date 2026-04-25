package com.javarush.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграционные тесты для GraphQL Query-операций.
 * Поднимает полный Spring-контекст с реальной H2-базой и данными из data.sql.
 * Запросы отправляются через MockMvc в виде HTTP POST на /graphql.
 *
 * GraphQL всегда отвечает HTTP 200, даже при ошибках.
 * Данные — в поле "data", ошибки — в поле "errors".
 */
@SpringBootTest
class GraphQlQueryTest {

    // MockMvc создается вручную через WebApplicationContext —
    // это альтернатива @AutoConfigureMockMvc, работающая в Spring Boot 4.x
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    /**
     * Вспомогательный метод: отправляет GraphQL-запрос
     * @param query
     * @return
     * @throws Exception
     */
    private org.springframework.test.web.servlet.ResultActions graphql(String query) throws Exception {
        return mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\":\"" + query + "\"}"));
    }

    @Test
    void allBooks_shouldReturnAllSevenBooks() throws Exception {
        graphql("{ allBooks { id title } }")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.allBooks").isArray())
                .andExpect(jsonPath("$.data.allBooks.length()").value(7))
                .andExpect(jsonPath("$.errors").doesNotExist());
    }

    @Test
    void allBooks_shouldReturnOnlyRequestedFields() throws Exception {
        // GraphQL — клиент выбирает только нужные поля (no over-fetching)
        graphql("{ allBooks { title } }")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.allBooks[0].title").exists())
                .andExpect(jsonPath("$.data.allBooks[0].id").doesNotExist());
    }

    @Test
    void allBooks_shouldResolveNestedAuthorField() throws Exception {
        // Вложенный запрос — поле author разрешается через геттер Book.getAuthor() (EAGER)
        graphql("{ allBooks { title author { name } } }")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.allBooks[0].author.name").isString())
                .andExpect(jsonPath("$.errors").doesNotExist());
    }

    @Test
    void bookById_shouldReturnCorrectBook() throws Exception {
        graphql("{ bookById(id: \\\"1\\\") { id title author { name } } }")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.bookById.title").value("Война и мир"))
                .andExpect(jsonPath("$.data.bookById.author.name").value("Лев Толстой"));
    }

    @Test
    void bookById_shouldReturnNull_whenNotFound() throws Exception {
        // GraphQL возвращает HTTP 200 даже при "не найдено" — данные null, без ошибок
        graphql("{ bookById(id: \\\"999\\\") { id title } }")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.bookById").isEmpty());
    }

    @Test
    void allAuthors_shouldReturnThreeAuthors() throws Exception {
        graphql("{ allAuthors { id name } }")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.allAuthors").isArray())
                .andExpect(jsonPath("$.data.allAuthors.length()").value(3));
    }

    @Test
    void allAuthors_withBooks_shouldInvokeSchemaMapping() throws Exception {
        // Вложенный запрос Author.books — разрешается через @SchemaMapping в BookController.
        // Демонстрирует Data Fetcher: для каждого автора вызывается booksForAuthor().
        graphql("{ allAuthors { name books { title } } }")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.allAuthors[0].name").value("Лев Толстой"))
                .andExpect(jsonPath("$.data.allAuthors[0].books.length()").value(3))
                .andExpect(jsonPath("$.data.allAuthors[1].books.length()").value(2))
                .andExpect(jsonPath("$.data.allAuthors[2].books.length()").value(2));
    }

    @Test
    void authorById_shouldReturnAuthorWithBio() throws Exception {
        graphql("{ authorById(id: \\\"2\\\") { name bio } }")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.authorById.name").value("Фёдор Достоевский"))
                .andExpect(jsonPath("$.data.authorById.bio").isString());
    }

    @Test
    void authorById_shouldReturnNull_whenNotFound() throws Exception {
        graphql("{ authorById(id: \\\"999\\\") { name } }")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.authorById").isEmpty());
    }
}
