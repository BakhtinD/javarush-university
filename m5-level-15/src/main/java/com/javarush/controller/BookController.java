package com.javarush.controller;

import com.javarush.entity.Author;
import com.javarush.entity.Book;
import com.javarush.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @QueryMapping
    public List<Book> allBooks() {
        return bookService.findAll();
    }

    @QueryMapping
    public Book bookById(@Argument Long id) {
        return bookService.findById(id).orElse(null);
    }

    @MutationMapping
    public Book addBook(@Argument String title, @Argument Long authorId) {
        return bookService.create(title, authorId);
    }

    @MutationMapping
    public Boolean deleteBook(@Argument Long id) {
        return bookService.delete(id);
    }

    // @SchemaMapping — Data Fetcher для вложенного поля.
    // typeName="Author", field="books": вызывается GraphQL-движком для каждого
    // объекта Author, когда клиент запрашивает поле "books".
    // Параметр метода (Author author) — это уже разрешенный объект-родитель.
    //
    // ВАЖНО: именно здесь возникает проблема N+1 при наивной реализации!
    // Для N авторов этот метод вызывается N раз, делая N отдельных SQL-запросов.
    // Решение — использовать DataLoader (паттерн batching), который объединяет
    // все запросы в один: SELECT * FROM BOOKS WHERE author_id IN (1, 2, ..., N)
    @SchemaMapping(typeName = "Author", field = "books")
    public List<Book> booksForAuthor(Author author) {
        return bookService.findByAuthorId(author.getId());
    }
}
