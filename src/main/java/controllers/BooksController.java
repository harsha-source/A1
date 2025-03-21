package controllers;

import jakarta.validation.Valid;
import models.Books;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import services.BookService;


@RestController
@RequestMapping("/books")
@Validated
public class BooksController {

    @Autowired
    private BookService bookService;

    // Add Book
    @PostMapping
    public ResponseEntity<?> addBook(@RequestBody @Valid Books book, UriComponentsBuilder uriBuilder) {
        return bookService.addBook(book, uriBuilder);
    }

    // Update Book
    @PutMapping("/{isbn}")
    public ResponseEntity<?> updateBook(@PathVariable String isbn, @RequestBody @Valid Books book) {
        return bookService.updateBook(isbn, book);
    }

    // Get Book by ISBN
    @GetMapping("/{isbn}")
    public ResponseEntity<?> getBookByIsbn(@PathVariable String isbn) {
        return bookService.getBookByIsbn(isbn);
    }

    // Retrieve Book using alternate route (same response)
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<?> getBookByIsbnAlternative(@PathVariable String isbn) {
        return bookService.getBookByIsbn(isbn);
    }
}

