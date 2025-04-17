package edu.vanzuriak.crud_operations.controller;

/*
    @author  olexander
    @project crud_operations
    @class   BookRestController
    @version 1.0.0
    @since 4/17/25 - 17 - 02
*/

import edu.vanzuriak.crud_operations.model.Book;
import edu.vanzuriak.crud_operations.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/books/")
@RequiredArgsConstructor
public class BookRestController {
    private final BookService bookService;

    @GetMapping
    public List<Book> showAll() {
        return bookService.getAll();
    }

    @GetMapping("{id}")
    public Book showOneById(@PathVariable String id) {
        return bookService.getById(id);
    }

    @PostMapping
    public Book insert(@RequestBody Book book) {
        return bookService.create(book);
    }

    @PutMapping
    public Book edit(@RequestBody Book book) {
        return bookService.update(book);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        bookService.deleteById(id);
    }

}
