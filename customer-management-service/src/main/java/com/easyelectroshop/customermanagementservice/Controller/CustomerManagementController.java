package com.easyelectroshop.customermanagementservice.Controller;

import com.easyelectroshop.customermanagementservice.Model.Customer;
import com.easyelectroshop.customermanagementservice.Service.CustomerManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customer-management")
public class CustomerManagementController {

    @Autowired
    CustomerManagementService customerManagementService;

    @PostMapping("/add-customer")
    public ResponseEntity<HttpStatusCode> saveCustomer(@RequestBody Customer customer){
        return ResponseEntity.status(customerManagementService.saveCustomer(customer)).build();
    }

}
