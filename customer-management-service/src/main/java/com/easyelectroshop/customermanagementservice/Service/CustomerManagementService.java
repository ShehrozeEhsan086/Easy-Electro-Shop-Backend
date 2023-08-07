package com.easyelectroshop.customermanagementservice.Service;

import com.easyelectroshop.customermanagementservice.Model.Customer;
import com.easyelectroshop.customermanagementservice.Repository.CustomerManagementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomerManagementService {

    @Autowired
    CustomerManagementRepository customerManagementRepository;

    public HttpStatusCode saveCustomer(Customer customer){
        log.info("ADDING NEW CUSTOMER WITH NAME "+customer.getFullName());
        try{
            customerManagementRepository.save(customer);
            log.info("SUCCESSFULLY ADDED CUSTOMER WITH CUSTOMER NAME "+customer.getFullName());
            return HttpStatusCode.valueOf(201);
        } catch (Exception ex){
            log.error("ERROR WHILE ADDING CUSTOMER WITH NAME "+customer.getFullName(),ex);
            return HttpStatusCode.valueOf(500);
        }
    }

}