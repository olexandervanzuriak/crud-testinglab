package edu.vanzuriak.crud_operations;

import edu.vanzuriak.crud_operations.model.Book;
import edu.vanzuriak.crud_operations.repository.BookRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataMongoTest
public class RepositoryTest {

    @Autowired
    BookRepository underTest;

    @BeforeEach
    void setup() {
        Book book1 = new Book("Book 1", "Author 1", "###Test");
        Book book2 = new Book("Book 2", "Author 2", "###Test");
        Book book3 = new Book("Book 3", "Author 3", "###Test");
        underTest.saveAll(List.of(book1, book2, book3));
    }

    @AfterEach
    void teardown() {
        underTest.deleteAll();
    }

    @Test
    void shouldSaveAndReturn3Books() {
        List<Book> all = underTest.findAll();
        long count = all.stream().filter(b -> b.getDescription().contains("###Test")).count();
        assertEquals(3, count);
    }

    @Test
    void shouldGenerateIdWhenSavingNewBook() {
        Book book = new Book("Generated Book", "Author X", "###Test");
        Book saved = underTest.save(book);
        assertNotNull(saved.getId());
        assertEquals(24, saved.getId().length());
    }

    @Test
    void shouldFindBookById() {
        Book book = underTest.findAll().get(0);
        Optional<Book> found = underTest.findById(book.getId());
        assertTrue(found.isPresent());
        assertEquals(book.getName(), found.get().getName());
    }

    @Test
    void shouldUpdateBookDescription() {
        Book book = underTest.findAll().get(0);
        book.setDescription("Updated ###Test");
        underTest.save(book);
        Book updated = underTest.findById(book.getId()).orElseThrow();
        assertEquals("Updated ###Test", updated.getDescription());
    }

    @Test
    void shouldDeleteBookById() {
        Book book = underTest.findAll().get(0);
        underTest.deleteById(book.getId());
        assertFalse(underTest.findById(book.getId()).isPresent());
    }

    @Test
    void shouldDeleteAllBooks() {
        underTest.deleteAll();
        assertEquals(0, underTest.findAll().size());
    }

    @Test
    void shouldSaveBookWithPredefinedId() {
        Book book = new Book("999999999999999999999999", "Predefined Book", "Author P", "###Test");
        underTest.save(book);
        Optional<Book> result = underTest.findById("999999999999999999999999");
        assertTrue(result.isPresent());
        assertEquals("Predefined Book", result.get().getName());
    }

    @Test
    void shouldReturnBooksWithSameAuthor() {
        Book book = new Book("Extra Book", "Author 1", "###Test");
        underTest.save(book);
        long count = underTest.findAll().stream()
                .filter(b -> b.getAuthor().equals("Author 1"))
                .count();
        assertEquals(2, count);
    }

    @Test
    void shouldNotFindBookWithInvalidId() {
        Optional<Book> result = underTest.findById("invalid_id_123456789012");
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldKeepIdWhenSavingExistingBook() {
        Book original = underTest.findAll().get(0);
        String originalId = original.getId();
        original.setName("Changed Name");
        underTest.save(original);
        Book updated = underTest.findById(originalId).orElseThrow();
        assertEquals("Changed Name", updated.getName());
        assertEquals(originalId, updated.getId());
    }
}
