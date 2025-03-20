package controllers;

import jakarta.validation.Valid;
import models.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.CustomerService;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // Add Customer
    @PostMapping
    public ResponseEntity<?> addCustomer(@RequestBody @Valid Customer customer) {
        try {
            Customer savedCustomer = customerService.addCustomer(customer);
            return ResponseEntity.status(201).body(savedCustomer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(422).body(e.getMessage());
        }
    }
//     Retrieve Customer by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    // Retrieve Customer by user ID
    @GetMapping
    public ResponseEntity<?> getCustomerByUserId(@RequestParam String userId) {
        return customerService.getCustomerByUserId(userId);
    }
}

