package com.javarush.service;

import com.javarush.entity.Author;
import com.javarush.entity.Book;
import com.javarush.repository.AuthorRepository;
import com.javarush.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Юнит-тест сервиса — Spring-контекст не поднимается.
 * Все зависимости (репозитории) заменены моками.
 * Тест быстрый и изолирует только бизнес-логику BookService.
 */
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private BookService bookService;

    private Author author;
    private Book book;

    @BeforeEach
    void setUp() {
        author = new Author(1L, "Лев Толстой", "Русский писатель", List.of());
        book = new Book(1L, "Война и мир", author);
    }

    @Test
    void findAll_shouldReturnAllBooks() {
        when(bookRepository.findAll()).thenReturn(List.of(book));

        List<Book> result = bookService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Война и мир");
        verify(bookRepository).findAll();
    }

    @Test
    void findById_shouldReturnBook_whenExists() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Optional<Book> result = bookService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Война и мир");
    }

    @Test
    void findById_shouldReturnEmpty_whenNotExists() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Book> result = bookService.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void create_shouldSaveBook_whenAuthorExists() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book result = bookService.create("Война и мир", 1L);

        assertThat(result.getTitle()).isEqualTo("Война и мир");
        assertThat(result.getAuthor().getName()).isEqualTo("Лев Толстой");
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void create_shouldThrowException_whenAuthorNotFound() {
        when(authorRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.create("Тест", 999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Author not found: 999");

        verify(bookRepository, never()).save(any());
    }

    @Test
    void delete_shouldReturnTrue_whenBookExists() {
        when(bookRepository.existsById(1L)).thenReturn(true);

        boolean result = bookService.delete(1L);

        assertThat(result).isTrue();
        verify(bookRepository).deleteById(1L);
    }

    @Test
    void delete_shouldReturnFalse_whenBookNotExists() {
        when(bookRepository.existsById(999L)).thenReturn(false);

        boolean result = bookService.delete(999L);

        assertThat(result).isFalse();
        verify(bookRepository, never()).deleteById(any());
    }

    @Test
    void findByAuthorId_shouldReturnBooksForAuthor() {
        when(bookRepository.findByAuthorId(1L)).thenReturn(List.of(book));

        List<Book> result = bookService.findByAuthorId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAuthor().getId()).isEqualTo(1L);
    }
}
