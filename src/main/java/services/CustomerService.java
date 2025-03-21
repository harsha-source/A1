package services;

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

    @Autowired
    private CustomerRepository customerRepository;

        public CustomerService(CustomerRepository customerRepository) {
            this.customerRepository = customerRepository;
        }


    public ResponseEntity<?> addCustomer(Customer customer, UriComponentsBuilder uriBuilder) {
        // Check if the userId already exists
        Optional<Customer> existingCustomer = customerRepository.getCustomerByUserId(customer.getUserId());
        if (existingCustomer.isPresent()) {
            return ResponseEntity.status(422).body("This user ID already exists in the system.");
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

