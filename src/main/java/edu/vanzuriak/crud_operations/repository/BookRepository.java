package edu.vanzuriak.crud_operations.repository;

/*
    @author  olexander
    @project crud_operations
    @class   BookRepository
    @version 1.0.0
    @since 4/17/25 - 16 - 24
*/

import edu.vanzuriak.crud_operations.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends MongoRepository<Book, String> {
    public boolean existsByName(String name);
}
