package repositories;

/**
 * Repository class for handling database operations related to Books.
 * This class provides methods to add, update, and retrieve book records from the database.
 * It uses Spring JDBC Template for database interactions.
 */

import models.Books;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BookRepository {
    // Spring JDBC Template for executing SQL queries
    private final JdbcTemplate jdbcTemplate;

    /**
     * Constructor for dependency injection of JdbcTemplate.
     * @param jdbcTemplate The JDBC template to be used for database operations
     */
    public BookRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Custom RowMapper to map database result set to Books objects.
     * Maps each column from the result set to the corresponding field in the Books class.
     */
    private final RowMapper<Books> bookRowMapper = (rs, rowNum) -> new Books(
            rs.getString("ISBN"), rs.getString("title"), rs.getString("author"),
            rs.getString("description"), rs.getString("genre"),
            rs.getDouble("price"), rs.getInt("quantity")
    );

    /**
     * Adds a new book to the database.
     * @param book The Books object containing book information to be added
     * @return Number of rows affected (1 if successful, 0 if failed)
     */
    public int addBook(Books book) {
        return jdbcTemplate.update("INSERT INTO books VALUES (?, ?, ?, ?, ?, ?, ?)",
                book.getISBN(), book.getTitle(), book.getAuthor(), book.getDescription(),
                book.getGenre(), book.getPrice(), book.getQuantity());
    }

    /**
     * Updates an existing book in the database.
     * @param book The Books object containing updated book information
     * @return Number of rows affected (1 if successful, 0 if no matching ISBN found)
     */
    public int updateBook(Books book) {
        return jdbcTemplate.update("UPDATE books SET title=?, author=?, description=?, genre=?, price=?, quantity=? WHERE ISBN=?",
                book.getTitle(), book.getAuthor(), book.getDescription(), book.getGenre(), book.getPrice(), book.getQuantity(), book.getISBN());
    }

    /**
     * Retrieves a book from the database by its ISBN.
     * Uses BeanPropertyRowMapper instead of the custom bookRowMapper.
     * @param isbn The ISBN of the book to retrieve
     * @return Books object if found, null if no book with the given ISBN exists
     */
    public Books getBookByISBN(String isbn) {
        String sql = "SELECT * FROM books WHERE ISBN = ?";
        List<Books> books = jdbcTemplate.query(sql, new Object[]{isbn}, new BeanPropertyRowMapper<>(Books.class));

        return books.isEmpty() ? null : books.get(0);
    }

}