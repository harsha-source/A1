package services;


import jakarta.validation.Valid;
import models.Books;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import repositories.BookRepository;

import java.util.Optional;

@Service
public class BookService {

    @Autowired(required = true)
    private BookRepository bookRepository;

    public ResponseEntity<?> addBook(@Valid Books book) {
        // Check if the ISBN already exists
        Optional<Books> existingBook = Optional.ofNullable(bookRepository.getBookByISBN(book.getISBN()));
        if (existingBook.isPresent()) {
            return ResponseEntity.status(422).body("This ISBN already exists in the system.");
        }
        // Save new book
        bookRepository.addBook(book);
        return ResponseEntity.status(201).body(book);
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
        return ResponseEntity.status(200).body(updatedBook);
    }

    public ResponseEntity<?> getBookByIsbn(String isbn) {
        Optional<Books> book = Optional.ofNullable(bookRepository.getBookByISBN(isbn));
        if (!book.isPresent()) {
            return ResponseEntity.status(404).body("ISBN not found.");
        }
        return ResponseEntity.status(200).body(book);
    }
}

