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
        books.add(new Book("1", "name1", "author1", "description1", "code1" ));
        books.add(new Book("2", "name2", "author2", "description2", "code2" ));
        books.add(new Book("3", "name3", "author3", "description3", "code3" ));
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
        if (bookRepository.existsByCode(request.code())) {
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
            if (updateDates == null) {
                updateDates = new ArrayList<>();
            }
            updateDates.add(LocalDateTime.now());

            Book bookToUpdate = Book.builder()
                    .id(request.id())
                    .name(request.name())
                    .author(request.author())
                    .description(request.description())
                    .code(request.code())
                    .createDate(bookPersisted.getCreateDate())
                    .updateDate(updateDates)
                    .build();

            return bookRepository.save(bookToUpdate);
        }
        return null;
    }

    private Book mapToBook(BookCreateRequest request) {
        return Book.builder()
                .name(request.name())
                .author(request.author())
                .description(request.description())
                .code(request.code())
                .build();

    }

    public void deleteById(String id) {
        bookRepository.deleteById(id);
    }
}
