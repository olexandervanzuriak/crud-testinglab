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


import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Document
public class Book {
    @Id
    private String id;
    private String name;
    private String author;
    private String description;

    private LocalDateTime createDate;
    private List<LocalDateTime> updateDate;

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

    @Builder
    public Book(String id, String name, String author, String description, LocalDateTime createDate, List<LocalDateTime> updateDate) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.description = description;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book item = (Book) o;
        return getId().equals(item.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
