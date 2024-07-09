package com.example.demo.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.model.dto.BookDto;
import com.example.demo.rest.book.BookController;
import com.example.demo.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Collections;
import java.util.Optional;

@WebMvcTest(BookController.class)
@ExtendWith(MockitoExtension.class)
@EnableWebMvc
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private ObjectMapper objectMapper;
    private BookDto bookDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle("Test Book");
        bookDto.setAuthor("Test Author");
        bookDto.setIsbn("123");
        bookDto.setCategory("category");

        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    void getAllBooks_shouldReturnListOfBooks() throws Exception {
        when(bookService.findAll()).thenReturn(Collections.singletonList(bookDto));

        mockMvc.perform(get("/v1/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Book"));
    }

    @Test
    void getBookById_shouldReturnBook_whenBookExists() throws Exception {
        when(bookService.findById(1L)).thenReturn(Optional.of(bookDto));

        mockMvc.perform(get("/v1/books/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    void getBookById_shouldReturnNotFound_whenBookDoesNotExist() throws Exception {
        when(bookService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/v1/books/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createBook_shouldReturnCreatedBook() throws Exception {
        when(bookService.save(any(BookDto.class))).thenReturn(bookDto);

        mockMvc.perform(post("/v1/books")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    void updateBook_shouldReturnUpdatedBook_whenBookExists() throws Exception {
        when(bookService.findById(1L)).thenReturn(Optional.of(bookDto));
        when(bookService.save(any(BookDto.class))).thenReturn(bookDto);

        mockMvc.perform(put("/v1/books/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    void updateBook_shouldReturnNotFound_whenBookDoesNotExist() throws Exception {
        when(bookService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/v1/books/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBook_shouldReturnOk_whenBookExists() throws Exception {
        when(bookService.findById(1L)).thenReturn(Optional.of(bookDto));

        mockMvc.perform(delete("/v1/books/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deleteBook_shouldReturnNotFound_whenBookDoesNotExist() throws Exception {
        when(bookService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/v1/books/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void borrowBook_shouldReturnBorrowedBook_whenBookCanBeBorrowed() throws Exception {
        when(bookService.borrowBook(1L)).thenReturn(bookDto);

        mockMvc.perform(post("/v1/books/borrow/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    void borrowBook_shouldReturnBadRequest_whenBookCannotBeBorrowed() throws Exception {
        when(bookService.borrowBook(1L)).thenThrow(new RuntimeException("Book is already borrowed"));

        mockMvc.perform(post("/v1/books/borrow/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnBook_shouldReturnReturnedBook_whenBookCanBeReturned() throws Exception {
        when(bookService.returnBook(1L)).thenReturn(bookDto);

        mockMvc.perform(post("/v1/books/return/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    void returnBook_shouldReturnBadRequest_whenBookCannotBeReturned() throws Exception {
        when(bookService.returnBook(1L)).thenThrow(new RuntimeException("Book is not borrowed"));

        mockMvc.perform(post("/v1/books/return/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}

