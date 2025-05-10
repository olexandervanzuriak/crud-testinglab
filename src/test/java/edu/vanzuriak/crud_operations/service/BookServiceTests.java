package edu.vanzuriak.crud_operations.service;

/*
    @author  olexander
    @project crud_operations
    @class   BookServiceTests
    @version 1.0.0
    @since 5/10/25 - 10 - 05
*/

import edu.vanzuriak.crud_operations.model.Book;
import edu.vanzuriak.crud_operations.repository.BookRepository;
import edu.vanzuriak.crud_operations.request.BookCreateRequest;
import edu.vanzuriak.crud_operations.request.BookUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BookServiceTests {

    @Autowired
    private BookRepository repository;

    @Autowired
    private BookService underTest;

    @BeforeEach
    void setUp() {
    }
    //  @AfterEach
    void tearsDown(){
        repository.deleteAll();
    }

    @Test
    void whenInsertNewItem_ThenCreateDateIsPresent() {
        //given
        BookCreateRequest request = new BookCreateRequest("Book 1", "Author 1", "Cool description 1");
        LocalDateTime now = LocalDateTime.now();
        // when
        Book createdBook = underTest.create(request);
        // then
        assertNotNull(createdBook);
        assertNotNull(createdBook.getId());
        assertEquals("Book 1", createdBook.getName());
        assertEquals("Author 1", createdBook.getAuthor());
        assertEquals("Cool description 1", createdBook.getDescription());
        assertNotNull(createdBook.getCreateDate());
        assertSame(LocalDateTime.class, createdBook.getCreateDate().getClass());
        assertTrue(createdBook.getCreateDate().isAfter(now));
        assertNotNull(createdBook.getUpdateDate());
        assertSame(ArrayList.class, createdBook.getUpdateDate().getClass());
        assertTrue(createdBook.getUpdateDate().isEmpty());

    }

    @Test
    void whenGetAll_thenReturnAllPersistedBooks() {
        underTest.create(new Book("Book A", "Author A", "Desc A"));
        underTest.create(new Book("Book B", "Author B", "Desc B"));
        underTest.create(new Book("Book C", "Author C", "Desc C"));

        var allBooks = underTest.getAll();
        assertNotNull(allBooks);
        assertTrue(allBooks.size() >= 3);
    }

    @Test
    void whenGetById_thenReturnCorrectBook() {
        Book book = underTest.getAll().get(0);
        Book found = underTest.getById(book.getId());
        assertNotNull(found);
        assertEquals(book.getName(), found.getName());
    }

    @Test
    void whenGetByInvalidId_thenReturnNull() {
        Book book = underTest.getById("nonexistent-id");
        assertNull(book);
    }

    @Test
    void whenCreateWithDuplicateName_thenReturnNull() {
        BookCreateRequest existingRequest = new BookCreateRequest("name1", "Author Original", "Original Desc");
        underTest.create(existingRequest);

        BookCreateRequest duplicateRequest = new BookCreateRequest("name1", "someAuthor", "someDesc");
        Book created = underTest.create(duplicateRequest);

        assertNull(created);
    }

    @Test
    void whenUpdateExistingBook_thenUpdateDateIsAppended() {
        Book original = underTest.getAll().get(0);

        if (original.getUpdateDate() == null) {
            original.setUpdateDate(new ArrayList<>());
            underTest.update(original);
            original = underTest.getById(original.getId());
        }

        int previousUpdateSize = original.getUpdateDate().size();

        BookUpdateRequest updateRequest = new BookUpdateRequest(
                original.getId(), "Updated Name", "Updated Author", "Updated Description"
        );

        Book updated = underTest.update(updateRequest);

        assertNotNull(updated);
        assertEquals("Updated Name", updated.getName());
        assertEquals("Updated Author", updated.getAuthor());
        assertEquals("Updated Description", updated.getDescription());
        assertEquals(original.getCreateDate(), updated.getCreateDate());
        assertNotNull(updated.getUpdateDate());
        assertEquals(previousUpdateSize + 1, updated.getUpdateDate().size());
    }

    @Test
    void whenUpdateNonExistingBook_thenReturnNull() {
        BookUpdateRequest updateRequest = new BookUpdateRequest(
                "nonexistent-id", "Name", "Author", "Desc"
        );
        Book updated = underTest.update(updateRequest);
        assertNull(updated);
    }

    @Test
    void whenDeleteById_thenBookIsRemoved() {
        Book book = underTest.getAll().get(0);
        underTest.deleteById(book.getId());
        Book deleted = underTest.getById(book.getId());
        assertNull(deleted);
    }

    @Test
    void whenCreateViaBookObject_thenReturnPersistedBook() {
        Book book = new Book("Some Book", "Some Author", "Some Desc");
        book.setCreateDate(LocalDateTime.now());
        book.setUpdateDate(new ArrayList<>());
        Book saved = underTest.create(book);
        assertNotNull(saved.getId());
    }

    @Test
    void whenUpdateViaBookObject_thenReturnUpdatedBook() {
        Book book = underTest.getAll().get(0);
        book.setName("Renamed");
        Book updated = underTest.update(book);
        assertEquals("Renamed", updated.getName());
    }

    @Test
    void whenCreateWithEmptyUpdateList_thenItRemainsEmpty() {
        BookCreateRequest request = new BookCreateRequest("Fresh Title", "New Author", "New Desc");
        Book created = underTest.create(request);
        assertNotNull(created);
        assertTrue(created.getUpdateDate().isEmpty());
    }

}
