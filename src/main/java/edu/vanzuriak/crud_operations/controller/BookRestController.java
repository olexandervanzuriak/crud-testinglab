package edu.vanzuriak.crud_operations.controller;

/*
    @author  olexander
    @project crud_operations
    @class   BookRestController
    @version 1.0.0
    @since 4/17/25 - 17 - 02
*/

import edu.vanzuriak.crud_operations.model.Book;
import edu.vanzuriak.crud_operations.request.BookCreateRequest;
import edu.vanzuriak.crud_operations.request.BookUpdateRequest;
import edu.vanzuriak.crud_operations.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/v1/books")
@RequiredArgsConstructor
public class BookRestController {
    private final BookService bookService;

    @GetMapping
    public List<Book> showAll() {
        return bookService.getAll();
    }

    @GetMapping("{id}")
    public Book showOneById(@PathVariable String id) {
        Book book = bookService.getById(id);
        if (book == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
        }
        return book;
    }

    @PostMapping
    public Book insert(@RequestBody Book book) {
        return bookService.create(book);
    }

    @PostMapping("/dto")
    public Book insert(@RequestBody BookCreateRequest request) {
        return bookService.create(request);
    }


    @PutMapping
    public Book edit(@RequestBody Book book) {
        return bookService.update(book);
    }

    @PutMapping("/dto")
    public Book edit(@RequestBody BookUpdateRequest request) {
        return bookService.update(request);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        bookService.deleteById(id);
    }

}
