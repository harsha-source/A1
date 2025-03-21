package services;

import models.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import repositories.CustomerRepository;

import java.util.*;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

        public CustomerService(CustomerRepository customerRepository) {
            this.customerRepository = customerRepository;
        }

        public Customer addCustomer(Customer customer) {
            // Check if the userId already exists
            Optional<Customer> existingCustomer = customerRepository.getCustomerByUserId(customer.getUserId());
            if (existingCustomer.isPresent()) {
                throw new IllegalArgumentException("This user ID already exists in the system.");
            }

            // Save customer and check if the insert was successful
            int rowsAffected = customerRepository.addCustomer(customer);
            long id = customerRepository.getCustomerByUserId(customer.getUserId()).get().getId();
            customer.setId(id);
            if (rowsAffected > 0) {
                return customer;
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

