package controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import models.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import services.CustomerService;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // Add Customer
    @PostMapping
    public ResponseEntity<?> addCustomer(@RequestBody @Valid Customer customer, UriComponentsBuilder uriBuilder) {
            return customerService.addCustomer(customer,uriBuilder);

    }
//     Retrieve Customer by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    // Retrieve Customer by user ID
    @GetMapping
    public ResponseEntity<?> getCustomerByUserId(@RequestParam @Email String userId) {
        return customerService.getCustomerByUserId(userId);
    }
}

