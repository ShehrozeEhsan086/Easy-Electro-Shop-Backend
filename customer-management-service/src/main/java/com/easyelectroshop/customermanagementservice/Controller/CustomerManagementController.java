package com.easyelectroshop.customermanagementservice.Controller;

import com.easyelectroshop.customermanagementservice.Model.Customer;
import com.easyelectroshop.customermanagementservice.Service.CustomerManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
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

    @GetMapping("/get-all")
    public ResponseEntity<List<Customer>> getAllCustomers(
            @RequestParam(value="pageNumber",defaultValue = "0",required = false) int pageNumber,
            @RequestParam(value="pageSize",defaultValue = "5",required = false) int pageSize,
            @RequestParam(value="sort",defaultValue = "lastUpdated",required = false) String sortBy)
    {
        int length = customerManagementService.getCustomersCount();
        if(length == 0){
            log.info("NO CUSTOMERS IN DATABASE");
            return ResponseEntity.ok(null);
        } else {
            List<Customer> customers = customerManagementService.getAllCustomers(pageNumber,pageSize,sortBy);
            return (customers != null) ? ResponseEntity.ok(customers) : ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/get-all-count")
    public ResponseEntity<Integer> getCustomersCount(){
        int length = customerManagementService.getCustomersCount();
        return (length >= 0) ? ResponseEntity.ok(length) : ResponseEntity.internalServerError().build();
    }

    @PutMapping("/update-customer")
    public ResponseEntity<HttpStatusCode> updateCustomer(@RequestBody Customer customer){
        return ResponseEntity.status(customerManagementService.updateCustomer(customer)).build();
    }

    @PutMapping("/complete-customer-profile")
    public ResponseEntity<HttpStatusCode> completeCustomerProfile(@RequestBody Customer customer){
        return ResponseEntity.status(customerManagementService.completeCustomerProfile(customer)).build();
    }

    @DeleteMapping("/delete-customer/{customerId}")
    public ResponseEntity<HttpStatusCode> deleteCustomer(@PathVariable UUID customerId){
        return ResponseEntity.status(customerManagementService.deleteCustomer(customerId)).build();
    }

}