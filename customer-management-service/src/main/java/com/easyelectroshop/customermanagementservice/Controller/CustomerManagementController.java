package com.easyelectroshop.customermanagementservice.Controller;

import com.easyelectroshop.customermanagementservice.Model.Customer;
import com.easyelectroshop.customermanagementservice.Service.CustomerManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customer-management")
public class CustomerManagementController {

    @Autowired
    CustomerManagementService customerManagementService;

    @PostMapping("/add-customer")
    public ResponseEntity<HttpStatusCode> saveCustomer(@RequestBody Customer customer){
        return ResponseEntity.status(customerManagementService.saveCustomer(customer)).build();
    }

    @GetMapping("/get-customer/{customerId}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable UUID customerId){
        Optional<Customer> customer = customerManagementService.getCustomerById(customerId);
        return(customer.isPresent()) ? ResponseEntity.ok(customer.get()) : ResponseEntity.notFound().build();
    }

    // Unfinished
}