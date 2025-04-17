package edu.vanzuriak.crud_operations.model;

/*
    @author  olexander
    @project crud_operations
    @class   Book
    @version 1.0.0
    @since 4/17/25 - 16 - 19
*/

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;


@Data
@NoArgsConstructor
@Getter
@Setter
@ToString
@Document
public class Book {
    @Id
    private String id;
    private String name;
    private String author;
    private String description;

    public Book(String id, String name, String author, String description) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.description = description;
    }

    public Book(String name, String author, String description) {
        this.name = name;
        this.author = author;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Book book)) return false;
        return Objects.equals(id, book.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
