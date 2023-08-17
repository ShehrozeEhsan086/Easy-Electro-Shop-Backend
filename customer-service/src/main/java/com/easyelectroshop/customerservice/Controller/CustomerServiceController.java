package com.easyelectroshop.customerservice.Controller;


import com.easyelectroshop.customerservice.DTO.Customer;
import com.easyelectroshop.customerservice.Service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customer")
public class CustomerServiceController {

    @Autowired
    CustomerService customerService;

    // ----------------  APIS FOR CUSTOMER MANAGEMENT SERVICE [[START]] --------------------

    @PostMapping("/add-customer")
    public ResponseEntity<HttpStatusCode> saveCustomer(@RequestBody Customer customer){
        return ResponseEntity.status(customerService.saveCustomer(customer)).build();
    }

}