package services;

/**
 * Service class that handles business logic for book operations.
 * Acts as an intermediary between controllers and the repository layer.
 * Provides methods for adding, updating, and retrieving books with appropriate HTTP responses.
 */

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

    /**
     * Injects the BookRepository to handle database operations.
     * Required=true ensures that the application fails to start if the dependency cannot be injected.
     */
    @Autowired(required = true)
    private BookRepository bookRepository;

    /**
     * Adds a new book to the system if the ISBN doesn't already exist.
     * Note: The @RequestBody annotation here in a service is unusual and typically belongs in a controller.
     *
     * @param book The book to be added, validated with bean validation
     * @param uriBuilder Builder for creating the location URI in the response
     * @return ResponseEntity with appropriate status code, headers, and body:
     *         - 201 Created with location header and book data if successful
     *         - 422 Unprocessable Entity with error message if ISBN already exists
     */
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

    /**
     * Updates an existing book's details if it exists in the system.
     * Validates that the ISBN in the path matches the ISBN in the book object.
     *
     * @param isbn The ISBN from the path parameter
     * @param book The updated book details, validated with bean validation
     * @return ResponseEntity with appropriate status code and body:
     *         - 200 OK with updated book data if successful
     *         - 400 Bad Request if ISBN in path doesn't match book object
     *         - 404 Not Found if book with given ISBN doesn't exist
     */
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
        return ResponseEntity.status(200).body(updatedBook);
    }

    /**
     * Retrieves a book by its ISBN.
     *
     * @param isbn The ISBN of the book to retrieve
     * @return ResponseEntity with appropriate status code and body:
     *         - 200 OK with book data wrapped in Optional if found
     *         - 404 Not Found with error message if no book with given ISBN exists
     */
    public ResponseEntity<?> getBookByIsbn(String isbn) {
        Optional<Books> book = Optional.ofNullable(bookRepository.getBookByISBN(isbn));
        if (!book.isPresent()) {
            return ResponseEntity.status(404).body("ISBN not found.");
        }
        return ResponseEntity.status(200).body(book);
    }
}