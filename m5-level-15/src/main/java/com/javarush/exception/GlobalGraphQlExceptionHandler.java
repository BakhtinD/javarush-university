package com.javarush.exception;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * Глобальный обработчик исключений для всех GraphQL-контроллеров.
 *
 * @ControllerAdvice + @GraphQlExceptionHandler — глобальный аналог
 * controller-level @GraphQlExceptionHandler из BookController.
 * Применяется ко ВСЕМ контроллерам, а не только к BookController.
 *
 * Порядок применения:
 * 1. Сначала ищется @GraphQlExceptionHandler в самом контроллере
 * 2. Затем — в @ControllerAdvice бинах
 *
 * Без любого из этих обработчиков Spring for GraphQL маскирует сообщение,
 * возвращая "INTERNAL_ERROR for <UUID>" вместо текста исключения.
 */
@ControllerAdvice
public class GlobalGraphQlExceptionHandler {

    @GraphQlExceptionHandler
    public GraphQLError handleIllegalArgument(IllegalArgumentException ex, DataFetchingEnvironment env) {
        return GraphqlErrorBuilder.newError(env)
                .message(ex.getMessage())
                .errorType(ErrorType.ValidationError)
                .build();
    }
}
