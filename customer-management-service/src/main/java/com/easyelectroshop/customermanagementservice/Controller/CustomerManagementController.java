package com.easyelectroshop.customermanagementservice.Controller;

import com.easyelectroshop.customermanagementservice.DTO.CustomerDTO;
import com.easyelectroshop.customermanagementservice.Model.Customer;
import com.easyelectroshop.customermanagementservice.Model.PaymentMethod;
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

    @GetMapping("/get-customer-by-id/{customerId}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable UUID customerId){
        CustomerDTO customer = customerManagementService.getCustomerById(customerId);
        return(customer != null) ? ResponseEntity.ok(customer) : ResponseEntity.notFound().build();
    }

    @GetMapping("/get-customer-by-email/{customerEmail}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable String customerEmail){
        return customerManagementService.getCustomerByEmail(customerEmail);
    }

    @GetMapping("/get-customer-payment-method/{customerId}")
    public ResponseEntity<PaymentMethod> getCustomerPaymentMethod(@PathVariable UUID customerId){
        return customerManagementService.getCustomerPaymentMethod(customerId);
    }

    @PutMapping("/add-order-info/{customerId}/{amount}")
    public ResponseEntity<HttpStatusCode> increaseOrderCountAmountOfCustomer(@PathVariable UUID customerId,
                                                                             @PathVariable double amount){
        return ResponseEntity.status(customerManagementService.increaseOrderCountAmountOfCustomer(customerId, amount)).build();
    }

    @PutMapping("/remove-order-info/{customerId}/{amount}")
    public ResponseEntity<HttpStatusCode> reduceOrderCountAmountOfCustomer(@PathVariable UUID customerId,
                                                                             @PathVariable double amount){
        return ResponseEntity.status(customerManagementService.decreaseOrderCountAmountOfCustomer(customerId,amount)).build();
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<CustomerDTO>> getAllCustomers(
            @RequestParam(value="pageNumber",defaultValue = "0",required = false) int pageNumber,
            @RequestParam(value="pageSize",defaultValue = "5",required = false) int pageSize,
            @RequestParam(value="sort",defaultValue = "fullName",required = false) String sortBy)
    {
        int length = customerManagementService.getCustomersCount();
        if(length == 0){
            log.info("NO CUSTOMERS IN DATABASE");
            return ResponseEntity.ok(null);
        } else {
            List<CustomerDTO> customers = customerManagementService.getAllCustomers(pageNumber,pageSize,sortBy);
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

    @GetMapping("/get-customer-name/{customerId}")
    public ResponseEntity<String> getCustomerName(@PathVariable UUID customerId){
        return customerManagementService.getCustomerNameById(customerId);
    }

}