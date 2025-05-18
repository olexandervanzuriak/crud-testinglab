package edu.vanzuriak.crud_operations.service;

/*
    @author  olexander
    @project crud_operations
    @class   BookServiceMockTests
    @version 1.0.0
    @since 5/18/25 - 09 - 36
*/

import edu.vanzuriak.crud_operations.model.Book;
import edu.vanzuriak.crud_operations.repository.BookRepository;
import edu.vanzuriak.crud_operations.request.BookCreateRequest;
import edu.vanzuriak.crud_operations.request.BookUpdateRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
public class BookServiceMockTests {

    @Mock
    private BookRepository mockRepository;

    @InjectMocks
    private BookService underTest;

    @Captor
    private ArgumentCaptor<Book> bookCaptor;

    private BookCreateRequest createRequest;
    private BookUpdateRequest updateRequest;
    private Book book;

    @BeforeEach
    void setup() {
        createRequest = new BookCreateRequest("BookName", "Author", "Description", "CODE123");
        book = Book.builder()
                .id("id123")
                .name(createRequest.name())
                .author(createRequest.author())
                .description(createRequest.description())
                .code(createRequest.code())
                .createDate(LocalDateTime.now())
                .updateDate(new ArrayList<>())
                .build();
    }

    @Test
    void whenCreateValidBook_thenSavesCorrectly() {
        when(mockRepository.existsByCode(createRequest.code())).thenReturn(false);
        when(mockRepository.save(any(Book.class))).thenReturn(book);

        Book saved = underTest.create(createRequest);

        verify(mockRepository).save(bookCaptor.capture());
        Book captured = bookCaptor.getValue();

        assertThat(captured.getName()).isEqualTo(createRequest.name());
        assertNotNull(saved);
        verify(mockRepository, times(1)).save(any(Book.class));
    }

    @Test
    void whenCreateBookWithExistingCode_thenReturnsNull() {
        when(mockRepository.existsByCode(createRequest.code())).thenReturn(true);

        Book result = underTest.create(createRequest);

        assertNull(result);
        verify(mockRepository, never()).save(any());
    }

    @Test
    void whenGetAll_thenReturnsAllBooks() {
        List<Book> mockList = List.of(book);
        when(mockRepository.findAll()).thenReturn(mockList);

        List<Book> result = underTest.getAll();

        assertEquals(1, result.size());
        verify(mockRepository).findAll();
    }

    @Test
    void whenGetByIdExists_thenReturnBook() {
        when(mockRepository.findById("id123")).thenReturn(Optional.of(book));

        Book result = underTest.getById("id123");

        assertNotNull(result);
        assertEquals(book.getName(), result.getName());
    }

    @Test
    void whenGetByIdMissing_thenReturnNull() {
        when(mockRepository.findById("invalid")).thenReturn(Optional.empty());

        Book result = underTest.getById("invalid");

        assertNull(result);
    }

    @Test
    void whenDeleteById_thenCallsRepository() {
        underTest.deleteById("id123");

        verify(mockRepository).deleteById("id123");
    }

    @Test
    void whenUpdateBookObject_thenSavesBook() {
        Book updated = book;
        updated.setName("New Name");

        when(mockRepository.save(updated)).thenReturn(updated);

        Book result = underTest.update(updated);

        assertEquals("New Name", result.getName());
        verify(mockRepository).save(updated);
    }

    @Test
    void whenUpdateRequestValidId_thenAppendsUpdateDate() {
        when(mockRepository.findById("id123")).thenReturn(Optional.of(book));
        when(mockRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        updateRequest = new BookUpdateRequest("id123", "Updated", "Updated Author", "Updated Desc", "UpdatedCode");

        Book updated = underTest.update(updateRequest);

        assertNotNull(updated);
        assertEquals("Updated", updated.getName());
        assertEquals(1, updated.getUpdateDate().size());
    }

    @Test
    void whenUpdateRequestInvalidId_thenReturnsNull() {
        when(mockRepository.findById("missing")).thenReturn(Optional.empty());

        updateRequest = new BookUpdateRequest("missing", "Name", "Author", "Desc", "Code");

        Book result = underTest.update(updateRequest);

        assertNull(result);
    }

    @Test
    void whenCreateBook_thenUpdateDateIsEmpty() {
        when(mockRepository.existsByCode(createRequest.code())).thenReturn(false);
        when(mockRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Book result = underTest.create(createRequest);

        assertTrue(result.getUpdateDate().isEmpty());
    }
}
