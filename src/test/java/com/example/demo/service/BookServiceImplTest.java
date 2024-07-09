package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.mapper.BookMapper;
import com.example.demo.model.Book;
import com.example.demo.model.dto.BookDto;
import com.example.demo.model.jpa.repository.BookRepository;
import com.example.demo.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class BookServiceImplTest {

    @Mock
    private BookMapper bookMapper;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;
    private BookDto bookDto;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setBorrowed(false);

        bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle("Test Book");
        bookDto.setAuthor("Test Author");
    }

    @Test
    void findAll_shouldReturnAllBooks() {
        when(bookRepository.findAll()).thenReturn(List.of(book));
        when(bookMapper.toDto(any(Book.class))).thenReturn(bookDto);

        var books = bookService.findAll();

        assertThat(books).hasSize(1);
        assertThat(books.getFirst().getTitle()).isEqualTo("Test Book");

        verify(bookRepository, times(1)).findAll();
        verify(bookMapper, times(1)).toDto(any(Book.class));
    }

    @Test
    void findById_shouldReturnBook_whenBookExists() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(bookMapper.toDto(any(Book.class))).thenReturn(bookDto);

        var foundBook = bookService.findById(1L);

        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getTitle()).isEqualTo("Test Book");

        verify(bookRepository, times(1)).findById(anyLong());
        verify(bookMapper, times(1)).toDto(any(Book.class));
    }

    @Test
    void findById_shouldReturnEmpty_whenBookDoesNotExist() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        var foundBook = bookService.findById(1L);

        assertThat(foundBook).isNotPresent();

        verify(bookRepository, times(1)).findById(anyLong());
        verify(bookMapper, never()).toDto(any(Book.class));
    }

    @Test
    void save_shouldSaveAndReturnBook() {
        when(bookMapper.toEntity(any(BookDto.class))).thenReturn(book);
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(bookMapper.toDto(any(Book.class))).thenReturn(bookDto);

        var savedBook = bookService.save(bookDto);

        assertThat(savedBook).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("Test Book");

        verify(bookMapper, times(1)).toEntity(any(BookDto.class));
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(bookMapper, times(1)).toDto(any(Book.class));
    }

    @Test
    void deleteById_shouldDeleteBook() {
        doNothing().when(bookRepository).deleteById(anyLong());

        bookService.deleteById(1L);

        verify(bookRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void borrowBook_shouldMarkBookAsBorrowed_whenBookExistsAndNotBorrowed() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(bookMapper.toDto(any(Book.class))).thenReturn(bookDto);

        var borrowedBook = bookService.borrowBook(1L);

        assertThat(borrowedBook).isNotNull();

        verify(bookRepository, times(1)).findById(anyLong());
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(bookMapper, times(1)).toDto(any(Book.class));
    }

    @Test
    void borrowBook_shouldThrowException_whenBookDoesNotExist() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.borrowBook(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Book not found");

        verify(bookRepository, times(1)).findById(anyLong());
        verify(bookRepository, never()).save(any(Book.class));
        verify(bookMapper, never()).toDto(any(Book.class));
    }

    @Test
    void borrowBook_shouldThrowException_whenBookIsAlreadyBorrowed() {
        book.setBorrowed(true);
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> bookService.borrowBook(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Book is already borrowed");

        verify(bookRepository, times(1)).findById(anyLong());
        verify(bookRepository, never()).save(any(Book.class));
        verify(bookMapper, never()).toDto(any(Book.class));
    }

    @Test
    void returnBook_shouldMarkBookAsNotBorrowed_whenBookExistsAndBorrowed() {
        book.setBorrowed(true);
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(bookMapper.toDto(any(Book.class))).thenReturn(bookDto);

        var returnedBook = bookService.returnBook(1L);

        assertThat(returnedBook).isNotNull();

        verify(bookRepository, times(1)).findById(anyLong());
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(bookMapper, times(1)).toDto(any(Book.class));
    }

    @Test
    void returnBook_shouldThrowException_whenBookDoesNotExist() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.returnBook(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Book not found");

        verify(bookRepository, times(1)).findById(anyLong());
        verify(bookRepository, never()).save(any(Book.class));
        verify(bookMapper, never()).toDto(any(Book.class));
    }

    @Test
    void returnBook_shouldThrowException_whenBookIsNotBorrowed() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> bookService.returnBook(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Book is not borrowed");

        verify(bookRepository, times(1)).findById(anyLong());
        verify(bookRepository, never()).save(any(Book.class));
        verify(bookMapper, never()).toDto(any(Book.class));
    }
}

