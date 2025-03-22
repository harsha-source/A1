package services;

/**
 * Service class that handles business logic for customer operations.
 * Provides methods for adding and retrieving customers with appropriate HTTP responses.
 * Acts as an intermediary between controllers and the repository layer.
 */

import models.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import repositories.CustomerRepository;

import java.net.URI;
import java.util.*;

@Service
public class CustomerService {

    /**
     * Injects the CustomerRepository to handle database operations.
     */
    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Constructor for dependency injection of CustomerRepository.
     * Note: This constructor is redundant with the @Autowired field above,
     * but provides an alternative way to inject dependencies.
     *
     * @param customerRepository The repository to handle customer data operations
     */
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }


    /**
     * Adds a new customer to the system if the userId doesn't already exist.
     *
     * @param customer The customer to be added
     * @param uriBuilder Builder for creating the location URI in the response
     * @return ResponseEntity with appropriate status code, headers, and body:
     *         - 201 Created with location header and customer data if successful
     *         - 422 Unprocessable Entity with error message if userId already exists
     * @throws RuntimeException If the database operation fails to insert the customer
     */
    public ResponseEntity<?> addCustomer(Customer customer, UriComponentsBuilder uriBuilder) {
        // Check if the userId already exists
        Optional<Customer> existingCustomer = customerRepository.getCustomerByUserId(customer.getUserId());
        if (existingCustomer.isPresent()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "This user ID already exists in the system.");
            return ResponseEntity.status(422).body(errorResponse);
        }

        // Save customer and check if the insert was successful
        int rowsAffected = customerRepository.addCustomer(customer);

        if (rowsAffected > 0) {
            // Get the generated ID from the database
            long id = customerRepository.getCustomerByUserId(customer.getUserId()).get().getId();
            customer.setId(id);

            // Build the location URI for the header
            URI location = uriBuilder
                    .path("/customers/{id}")
                    .buildAndExpand(id)
                    .toUri();

            // Return 201 Created status, Location header, and customer in body
            return ResponseEntity
                    .created(location)
                    .body(customer);
        } else {
            throw new RuntimeException("Failed to save customer");
        }
    }

    /**
     * Retrieves a customer by their numeric ID.
     * Includes input validation and exception handling.
     *
     * @param id The numeric ID of the customer to retrieve
     * @return ResponseEntity with appropriate status code and body:
     *         - 200 OK with customer data if found
     *         - 400 Bad Request if ID is null, non-positive, or invalid
     *         - 404 Not Found if no customer with given ID exists
     */
    public ResponseEntity<?> getCustomerById(Long id) {
        Optional<Customer> customer;
        if (id == null || id <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid customer ID"));
        }
        try {
            customer = customerRepository.getCustomerById(id);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Customer with ID " + id + " not found.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid customer ID.");
        }

        return customer.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Customer not found")));
    }


    /**
     * Retrieves a customer by their userId (username or external identifier).
     * Includes input validation and exception handling.
     *
     * @param userId The userId of the customer to retrieve
     * @return ResponseEntity with appropriate status code and body:
     *         - 200 OK with customer data if found
     *         - 400 Bad Request if userId is null, empty, or invalid
     *         - 404 Not Found if no customer with given userId exists
     */
    public ResponseEntity<?> getCustomerByUserId(String userId) {
        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid customer ID"));
        }

        Optional<Customer> customer;
        try {
            customer = customerRepository.getCustomerByUserId(userId);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Customer with User ID " + userId + " not found."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid customer ID."));
        }

        return customer.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Customer not found")));
    }



}