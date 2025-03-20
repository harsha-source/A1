package models;



import jakarta.validation.constraints.*;
import lombok.Data;


@Data
public class Books {

    @NotBlank
    private String ISBN;

    @NotBlank
    private String title;


    @NotBlank(message = "Author cannot be null")
    private String author;

    @NotBlank
    private String description;

    @NotBlank
    private String genre;


    @Digits(integer = 6, fraction = 2)
    @NotNull(message = "Price cannot be null")
    private double price;

    @Min(0)
    private int quantity;

    public Books() {}

    public Books(String isbn, String title, String author, String description, String genre, double price, int quantity) {
        this.ISBN = isbn;
        this.title = title;
        this.author = author;
        this.description = description;
        this.genre = genre;
        this.price = price;
        this.quantity = quantity;
    }
}

