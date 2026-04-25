package com.javarush.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Интеграционные тесты для GraphQL Mutation-операций.
 *
 * @DirtiesContext — после каждого теста Spring-контекст пересоздается,
 * чтобы изменения в базе (INSERT/DELETE) не влияли на следующие тесты.
 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class GraphQlMutationTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    private org.springframework.test.web.servlet.ResultActions graphql(String query) throws Exception {
        return mockMvc.perform(post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"query\":\"" + query + "\"}"));
    }


    @Test
    void addAuthor_shouldCreateAndReturnAuthor() throws Exception {
        graphql("mutation { addAuthor(name: \\\"Иван Тургенев\\\", bio: \\\"Русский писатель\\\") { id name bio } }")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.addAuthor.id").value(notNullValue()))
                .andExpect(jsonPath("$.data.addAuthor.name").value("Иван Тургенев"))
                .andExpect(jsonPath("$.data.addAuthor.bio").value("Русский писатель"))
                .andExpect(jsonPath("$.errors").doesNotExist());
    }

    @Test
    void addAuthor_withoutBio_shouldCreateAuthorWithNullBio() throws Exception {
        // bio — необязательное поле (String без ! в схеме)
        graphql("mutation { addAuthor(name: \\\"Николай Гоголь\\\") { id name bio } }")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.addAuthor.name").value("Николай Гоголь"))
                .andExpect(jsonPath("$.data.addAuthor.bio").doesNotExist());
    }


    @Test
    void addBook_shouldCreateAndReturnBook() throws Exception {
        // authorId=1 — Лев Толстой (из data.sql)
        graphql("mutation { addBook(title: \\\"Детство\\\", authorId: \\\"1\\\") { id title author { name } } }")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.addBook.title").value("Детство"))
                .andExpect(jsonPath("$.data.addBook.author.name").value("Лев Толстой"))
                .andExpect(jsonPath("$.errors").doesNotExist());
    }

    @Test
    void addBook_shouldReturnError_whenAuthorNotFound() throws Exception {
        // GraphQL возвращает HTTP 200, но data.addBook = null и заполнено поле errors.
        // Сообщение доступно благодаря GraphQlExceptionHandler (DataFetcherExceptionResolverAdapter),
        // который переопределяет дефолтное маскирование ошибок Spring for GraphQL.
        graphql("mutation { addBook(title: \\\"Тест\\\", authorId: \\\"999\\\") { id title } }")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.addBook").isEmpty())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].message").value(containsString("Author not found")))
                .andExpect(jsonPath("$.errors[0].extensions.classification").value("ValidationError"));
    }

    @Test
    void deleteBook_shouldReturnTrue_whenBookExists() throws Exception {
        // Удаляем книгу с id=1 ("Война и мир"), загруженную из data.sql
        graphql("mutation { deleteBook(id: \\\"1\\\") }")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.deleteBook").value(true))
                .andExpect(jsonPath("$.errors").doesNotExist());
    }

    @Test
    void deleteBook_shouldReturnFalse_whenBookNotFound() throws Exception {
        graphql("mutation { deleteBook(id: \\\"999\\\") }")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.deleteBook").value(false));
    }


    @Test
    void fullScenario_addAuthorAddBookDeleteBook() throws Exception {
        // 1. Добавляем нового автора — ожидаем id=4 (3 уже есть в data.sql)
        MvcResult addAuthorResult = graphql(
                "mutation { addAuthor(name: \\\"Иван Тургенев\\\", bio: \\\"Писатель\\\") { id } }")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.addAuthor.id").value(notNullValue()))
                .andReturn();

        // Извлекаем id нового автора из ответа
        String response = addAuthorResult.getResponse().getContentAsString();
        String authorId = response.replaceAll(".*\"id\":\"?(\\d+)\"?.*", "$1");

        // 2. Добавляем книгу новому автору
        graphql("mutation { addBook(title: \\\"Отцы и дети\\\", authorId: \\\"" + authorId + "\\\") { id title author { name } } }")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.addBook.title").value("Отцы и дети"))
                .andExpect(jsonPath("$.data.addBook.author.name").value("Иван Тургенев"));

        // 3. Удаляем добавленную книгу (id=8, так как в data.sql 7 книг)
        graphql("mutation { deleteBook(id: \\\"8\\\") }")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.deleteBook").value(true));
    }
}
