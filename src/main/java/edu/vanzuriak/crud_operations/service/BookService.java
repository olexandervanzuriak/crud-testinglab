package edu.vanzuriak.crud_operations.service;

/*
    @author  olexander
    @project crud_operations
    @class   BookService
    @version 1.0.0
    @since 4/17/25 - 16 - 26
*/

import edu.vanzuriak.crud_operations.model.Book;
import edu.vanzuriak.crud_operations.repository.BookRepository;
import edu.vanzuriak.crud_operations.request.BookCreateRequest;
import edu.vanzuriak.crud_operations.request.BookUpdateRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    private List<Book> books = new ArrayList<>();
    {
        books.add(new Book("name1", "author1", "description1"));
        books.add(new Book("2", "name2", "author2", "description2"));
        books.add(new Book("3", "name3", "author3", "description3"));
    };

    @PostConstruct
    public void init() {
        bookRepository.deleteAll();
        bookRepository.saveAll(books);
    }

    public List<Book> getAll() {
        return bookRepository.findAll();
    }

    public Book getById(String id) {
        return bookRepository.findById(id).orElse(null);
    }

    public Book create(Book book) {
        return bookRepository.save(book);
    }

    public Book create(BookCreateRequest request) {
        if (bookRepository.existsByName(request.name())) {
            return null;
        }
        Book book = mapToBook(request);
        book.setCreateDate(LocalDateTime.now());
        book.setUpdateDate(new ArrayList<LocalDateTime>());
        return bookRepository.save(book);
    }

    public Book update(Book book) {
        return bookRepository.save(book);
    }

    public Book update(BookUpdateRequest request) {
        Book bookPersisted = bookRepository.findById(request.id()).orElse(null);
        if (bookPersisted != null) {
            List<LocalDateTime> updateDates = bookPersisted.getUpdateDate();
            updateDates.add(LocalDateTime.now());
            Book bookToUpdate =
                    Book.builder()
                            .id(request.id())
                            .name(request.name())
                            .author(request.author())
                            .description(request.description())
                            .createDate(bookPersisted.getCreateDate())
                            .updateDate(updateDates)
                            .build();
            return bookRepository.save(bookToUpdate);

        }
        return null;
    }

    private Book mapToBook(BookCreateRequest request) {
        Book book = new Book(request.name(), request.author(), request.description());
        return book;
    }

    public void deleteById(String id) {
        bookRepository.deleteById(id);
    }
}
