package com.javarush.controller;

import com.javarush.entity.Author;
import com.javarush.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

// @Controller (не @RestController!) — Spring for GraphQL использует стандартный @Controller
@Controller
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    // @QueryMapping — связывает метод с полем "allAuthors" в type Query схемы.
    // Имя метода должно совпадать с именем поля в схеме.
    @QueryMapping
    public List<Author> allAuthors() {
        return authorService.findAll();
    }

    // @Argument — связывает аргумент GraphQL-запроса с параметром Java-метода.
    // GraphQL передает id как String (тип ID), Spring автоматически конвертирует в Long.
    @QueryMapping
    public Author authorById(@Argument Long id) {
        return authorService.findById(id).orElse(null);
    }

    // @MutationMapping — связывает метод с полем "addAuthor" в type Mutation схемы.
    @MutationMapping
    public Author addAuthor(@Argument String name, @Argument String bio) {
        return authorService.create(name, bio);
    }
}
