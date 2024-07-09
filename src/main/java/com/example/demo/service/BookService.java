package com.example.demo.service;

import com.example.demo.model.dto.BookDto;
import java.util.List;
import java.util.Optional;

public interface BookService {
    List<BookDto> findAll();
    Optional<BookDto> findById(Long id);
    BookDto save(BookDto book);
    void deleteById(Long id);
    BookDto borrowBook(Long id);
    BookDto returnBook(Long id);
}
