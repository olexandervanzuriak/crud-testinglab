package edu.vanzuriak.crud_operations;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.vanzuriak.crud_operations.model.Book;
import edu.vanzuriak.crud_operations.repository.BookRepository;
import edu.vanzuriak.crud_operations.request.BookCreateRequest;
import edu.vanzuriak.crud_operations.request.BookUpdateRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*
    @author  olexander
    @project crud_operations
    @class   IntegrationTests
    @version 1.0.0
    @since 5/18/25 - 09 - 44
*/


@SpringBootTest
@AutoConfigureMockMvc
class IntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository repository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private List<Book> books = new ArrayList<>();

    @BeforeEach
    void setUp() {
        books.add(new Book("1", "1984", "Orwell", "Dystopia", "code1984"));
        books.add(new Book("2", "Brave New World", "Huxley", "Sci-Fi", "codeBNW"));
        books.add(new Book("3", "Fahrenheit 451", "Bradbury", "Burn books", "codeF451"));
        repository.saveAll(books);
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void shouldCreateNewBook() throws Exception {
        BookCreateRequest request = new BookCreateRequest("Dune", "Herbert", "Epic Sci-Fi", "codeDUNE");

        mockMvc.perform(post("/api/v1/books/dto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Book book = repository.findAll().stream()
                .filter(b -> b.getCode().equals("codeDUNE")).findFirst().orElse(null);

        assertNotNull(book);
        assertThat(book.getName()).isEqualTo(request.name());
    }

    @Test
    void shouldNotCreateBookWithDuplicateCode() throws Exception {
        BookCreateRequest request = new BookCreateRequest("Another 1984", "Somebody", "Copy", "code1984");

        mockMvc.perform(post("/api/v1/books/dto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        List<Book> all = repository.findAll();
        assertThat(all.stream().filter(b -> b.getCode().equals("code1984")).count()).isEqualTo(1);
    }

    @Test
    void shouldUpdateBook() throws Exception {
        Book existing = books.get(0);
        BookUpdateRequest request = new BookUpdateRequest(
                existing.getId(), "1984 Updated", "Orwell", "Updated Desc", "code1984");

        mockMvc.perform(put("/api/v1/books/dto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Book updated = repository.findById(existing.getId()).orElse(null);
        assertNotNull(updated);
        assertThat(updated.getName()).isEqualTo("1984 Updated");
        assertFalse(updated.getUpdateDate().isEmpty());
    }

    @Test
    void shouldNotUpdateMissingBook() throws Exception {
        BookUpdateRequest request = new BookUpdateRequest(
                "nonexistent-id", "NoBook", "NoAuthor", "NoDesc", "codeXXX");

        mockMvc.perform(put("/api/v1/books/dto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Book result = repository.findById("nonexistent-id").orElse(null);
        assertNull(result);
    }

    @Test
    void shouldGetBookById() throws Exception {
        Book book = books.get(0);

        mockMvc.perform(get("/api/v1/books/" + book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(book.getName()));
    }

    @Test
    void shouldReturn404ForMissingBook() throws Exception {
        mockMvc.perform(get("/api/v1/books/missing-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteBookById() throws Exception {
        Book book = books.get(1);

        mockMvc.perform(delete("/api/v1/books/" + book.getId()))
                .andExpect(status().isOk());

        assertThat(repository.existsById(book.getId())).isFalse();
    }

    @Test
    void shouldNotFailOnMissingDelete() throws Exception {
        mockMvc.perform(delete("/api/v1/books/missing-id"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnAllBooksSortedByName() throws Exception {
        mockMvc.perform(get("/api/v1/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("1984"))
                .andExpect(jsonPath("$[1].name").value("Brave New World"))
                .andExpect(jsonPath("$[2].name").value("Fahrenheit 451"));
    }

    @Test
    void shouldIgnoreDuplicateBookCodeAndNotCreateNewBook() throws Exception {
        // Initial book with code1984 is already in the setup data
        BookCreateRequest duplicateCodeRequest = new BookCreateRequest(
                "Fake Book", "Fake Author", "Fake Desc", "code1984"
        );

        long countBefore = repository.count();

        mockMvc.perform(post("/api/v1/books/dto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateCodeRequest)))
                .andExpect(status().isOk());

        long countAfter = repository.count();

        // Assert that count remains the same, duplicate was not saved
        assertEquals(countBefore, countAfter, "Duplicate code book should not be added");
    }

}