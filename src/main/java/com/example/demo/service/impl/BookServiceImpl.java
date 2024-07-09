package com.example.demo.service.impl;

import com.example.demo.mapper.BookMapper;
import com.example.demo.model.dto.BookDto;
import com.example.demo.model.jpa.repository.BookRepository;
import com.example.demo.service.BookService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookServiceImpl implements BookService {

    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private BookRepository bookRepository;

    @Override
    public List<BookDto> findAll() {
       return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<BookDto> findById(Long id) {
        return bookRepository.findById(id).map(bookMapper::toDto);
    }

    @Override
    public BookDto save(BookDto bookDto) {
        var book = bookMapper.toEntity(bookDto);
        book.setBorrowed(false);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public BookDto borrowBook(Long id) {
        var book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));
        if (book.getBorrowed()) {
            throw new RuntimeException("Book is already borrowed");
        }
        book.setBorrowed(true);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public BookDto returnBook(Long id) {
        var book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));
        if (!book.getBorrowed()) {
            throw new RuntimeException("Book is not borrowed");
        }
        book.setBorrowed(false);
        return bookMapper.toDto(bookRepository.save(book));
    }
}
