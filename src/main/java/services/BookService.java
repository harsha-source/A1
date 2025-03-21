package services;


import jakarta.validation.Valid;
import models.Books;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriComponentsBuilder;
import repositories.BookRepository;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class BookService {

    @Autowired(required = true)
    private BookRepository bookRepository;

    public ResponseEntity<?> addBook(@Valid @RequestBody Books book, UriComponentsBuilder uriBuilder) {
        // Check if the ISBN already exists
        Optional<Books> existingBook = Optional.ofNullable(bookRepository.getBookByISBN(book.getISBN()));
        if (existingBook.isPresent()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "This ISBN already exists in the system.");
            return ResponseEntity.status(422).body(errorResponse);
        }

        // Save new book
        bookRepository.addBook(book);

        // Build the location URI for the header
        URI location = uriBuilder
                .path("/books/{isbn}")
                .buildAndExpand(book.getISBN())
                .toUri();

        // Return 201 Created status, Location header, and book in body
        return ResponseEntity
                .created(location)
                .body(book);
    }

    public ResponseEntity<?> updateBook(String isbn, @Valid Books book) {

        if(!book.getISBN().equals(isbn)) {
            return ResponseEntity.status(400).body("ISBN does not match.");
        }
        // Find existing book by ISBN
        Optional<Books> existingBook = Optional.ofNullable(bookRepository.getBookByISBN(isbn));
        if (!existingBook.isPresent()) {
            return ResponseEntity.status(404).body("ISBN not found.");
        }
        // Update and save the book
        Books updatedBook = existingBook.get();
        updatedBook.setTitle(book.getTitle());
        updatedBook.setAuthor(book.getAuthor());
        updatedBook.setDescription(book.getDescription());
        updatedBook.setGenre(book.getGenre());
        updatedBook.setPrice(book.getPrice());
        updatedBook.setQuantity(book.getQuantity());

        bookRepository.updateBook(updatedBook);
        return ResponseEntity.status(201).body(updatedBook);
    }

    public ResponseEntity<?> getBookByIsbn(String isbn) {
        Optional<Books> book = Optional.ofNullable(bookRepository.getBookByISBN(isbn));
        if (!book.isPresent()) {
            return ResponseEntity.status(404).body("ISBN not found.");
        }
        return ResponseEntity.status(200).body(book);
    }
}

