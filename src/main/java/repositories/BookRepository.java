package repositories;




import models.Books;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BookRepository {
    private final JdbcTemplate jdbcTemplate;

    public BookRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Books> bookRowMapper = (rs, rowNum) -> new Books(
            rs.getString("ISBN"), rs.getString("title"), rs.getString("author"),
            rs.getString("description"), rs.getString("genre"),
            rs.getDouble("price"), rs.getInt("quantity")
    );

    public int addBook(Books book) {
        return jdbcTemplate.update("INSERT INTO books VALUES (?, ?, ?, ?, ?, ?, ?)",
                book.getISBN(), book.getTitle(), book.getAuthor(), book.getDescription(),
                book.getGenre(), book.getPrice(), book.getQuantity());
    }

    public int updateBook(Books book) {
        return jdbcTemplate.update("UPDATE books SET title=?, author=?, description=?, genre=?, price=?, quantity=? WHERE ISBN=?",
                book.getTitle(), book.getAuthor(), book.getDescription(), book.getGenre(), book.getPrice(), book.getQuantity(), book.getISBN());
    }
    public Books getBookByISBN(String isbn) {
        String sql = "SELECT * FROM books WHERE ISBN = ?";
        List<Books> books = jdbcTemplate.query(sql, new Object[]{isbn}, new BeanPropertyRowMapper<>(Books.class));

        return books.isEmpty() ? null : books.get(0);
    }

}
