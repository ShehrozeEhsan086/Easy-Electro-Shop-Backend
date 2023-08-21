package com.easyelectroshop.customermanagementservice.Service;

import com.easyelectroshop.customermanagementservice.Model.Customer;
import com.easyelectroshop.customermanagementservice.Repository.CustomerManagementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class CustomerManagementService {

    @Autowired
    CustomerManagementRepository customerManagementRepository;

    private boolean validateCustomerProfileCompleteness(Customer customer){
        return customer.getFullName() != null
                && customer.getEmail() != null
                && customer.getPhoneNumber() != null
                && customer.getGender() != null;
    }

    public List<Customer> getAllCustomers(int pageNumber, int pageSize, String sortBy){
        log.info("GETTING ALL CUSTOMERS");
        try{
            if(pageSize == -1){
                pageSize = Integer.MAX_VALUE;
            }
            List<Customer> customers = customerManagementRepository.findAllPaginated(sortBy,pageSize,pageNumber);
            log.info("SUCCESSFULLY RETRIEVED ALL CUSTOMERS");
            return customers;
        } catch (Exception ex){
            log.error("COULD NOT RETRIEVE ALL CUSTOMERS",ex);
            return null;
        }
    }

    public Optional<Customer> getCustomerById(UUID customerId){
        log.info("GETTING CUSTOMER WITH CUSTOMER_ID "+customerId);
        Optional<Customer> customer = customerManagementRepository.findById(customerId);
        if(customer.isPresent()){
            log.info("SUCCESSFULLY RETRIEVED CUSTOMER WITH CUSTOMER_ID "+customerId);
            return customer;
        } else {
            log.error("COULD NOT FIND CUSTOMER WITH CUSTOMER_ID "+customerId);
            return Optional.empty();
        }
    }

    public ResponseEntity<Optional<Customer>> getCustomerByEmail(String customerEmail){
        log.info("GETTING CUSTOMER WITH CUSTOMER_EMAIL "+customerEmail);
        try{
            Optional<Customer> customer = customerManagementRepository.findByEmail(customerEmail);
            if(customer.isPresent()){
                log.info("SUCCESSFULLY RETRIEVED CUSTOMER WITH CUSTOMER_EMAIL "+customerEmail);
                return ResponseEntity.ok(customer);
            } else {
                log.error("COULD NOT FIND CUSTOMER WITH CUSTOMER_EMAIL "+customerEmail);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex){
            log.error("ERROR WHILE GETTING CUSTOMER WITH CUSTOMER_EMAIL "+customerEmail, ex);
            return ResponseEntity.internalServerError().build();
        }

    }

    public int getCustomersCount() {
        log.info("GETTING CUSTOMERS COUNT");
        try{
            int length = customerManagementRepository.findAll().size();
            log.info("SUCCESSFULLY RETRIEVED CUSTOMERS COUNT");
            return length;
        } catch (Exception ex){
            log.error("COULD NOT RETRIEVE CUSTOMERS COUNT",ex);
            return 0;
        }
    }

    public HttpStatusCode saveCustomer(Customer customer){
        log.info("ADDING NEW CUSTOMER WITH DATA "+customer.toString());
        try{
            customerManagementRepository.save(customer);
            log.info("SUCCESSFULLY ADDED CUSTOMER WITH CUSTOMER EMAIL "+customer.getEmail());
            return HttpStatusCode.valueOf(201);
        } catch (Exception ex){
            log.error("ERROR WHILE ADDING CUSTOMER WITH EMAIL "+customer.getEmail(),ex);
            return HttpStatusCode.valueOf(500);
        }
    }

    public HttpStatusCode completeCustomerProfile(Customer customer){
        log.info("COMPLETING CUSTOMER PROFILE WITH CUSTOMER_ID "+customer.getCustomerId());
        try{
            Optional<Customer> tempCustomer = customerManagementRepository.findById(customer.getCustomerId());
            if (tempCustomer.isPresent()){
                if (customer.getEmail().equals(tempCustomer.get().getEmail())){
                    if (validateCustomerProfileCompleteness(customer)){
                        customer.setProfileComplete(true);
                        customerManagementRepository.save(customer);
                        log.info("SUCCESSFULLY COMPLETED CUSTOMER PROFILE WITH CUSTOMER_ID "+customer.getCustomerId());
                        return HttpStatusCode.valueOf(200);
                    } else {
                        log.error("ERROR COMPLETING CUSTOMER PROFILE AS ATTRIBUTE REQUIREMENTS WERE NOT MET FOR CUSTOMER_ID "+customer.getCustomerId());
                        return HttpStatusCode.valueOf(422);
                    }
                } else {
                    log.error("ERROR COMPLETING PROFILE FOR CUSTOMER WITH CUSTOMER_ID "+ customer.getCustomerId() +", EMAIL CANNOT BE CHANGED");
                    return HttpStatusCode.valueOf(400);
                }
            } else {
                log.error("ERROR COMPLETING CUSTOMER PROFILE AS CUSTOMER WAS NOT FOUND");
                return HttpStatusCode.valueOf(404);
            }
        } catch (Exception ex){
            log.error("ERROR COMPLETING CUSTOMER PROFILE WITH CUSTOMER_ID "+customer.getCustomerId(),ex);
            return HttpStatusCode.valueOf(500);
        }
    }

    public HttpStatusCode updateCustomer(Customer customer) {
        log.info("UPDATING CUSTOMER WITH CUSTOMER_ID "+customer.getCustomerId());
        try{
            Optional<Customer> tempCustomer = customerManagementRepository.findById(customer.getCustomerId());
            if(tempCustomer.isPresent()){
                if (customer.getEmail().equals(tempCustomer.get().getEmail())) {
                    if (validateCustomerProfileCompleteness(customer)) {
                        customerManagementRepository.save(customer);
                        log.info("SUCCESSFULLY EDITED CUSTOMER WITH CUSTOMER_ID " + customer.getCustomerId());
                        return HttpStatusCode.valueOf(200);
                    } else {
                        log.error("ERROR EDITING CUSTOMER AS PROFILE ATTRIBUTE REQUIREMENTS WERE NOT MET FOR CUSTOMER_ID " + customer.getCustomerId());
                        return HttpStatusCode.valueOf(422);
                    }
                } else {
                    log.error("ERROR COMPLETING PROFILE FOR CUSTOMER WITH CUSTOMER_ID "+ customer.getCustomerId() +", EMAIL CANNOT BE CHANGED");
                    return HttpStatusCode.valueOf(400);
                }
            } else {
                log.error("ERROR EDITING CUSTOMER WITH CUSTOMER_ID "+customer.getCustomerId()+"COULD NOT FIND GIVEN CUSTOMER");
                return HttpStatusCode.valueOf(404);
            }
        } catch (Exception ex){
            log.error("ERROR EDITING CUSTOMER WITH CUSTOMER_ID "+customer.getCustomerId(),ex);
            return HttpStatusCode.valueOf(500);
        }
    }

    public HttpStatusCode deleteCustomer(UUID customerId){
        log.info("DELETING CUSTOMER WITH CUSTOMER_ID "+customerId);
        try{
            Optional<Customer> customer = customerManagementRepository.findById(customerId);
            if(customer.isPresent()){
                customerManagementRepository.deleteById(customerId);
                log.info("SUCCESSFULLY DELETED CUSTOMER WITH CUSTOMER_ID "+customerId);
                return HttpStatusCode.valueOf(200);
            } else {
                log.error("COULD NOT DELETE CUSTOMER WITH CUSTOMER_ID "+customerId+", CUSTOMER NOT FOUND!");
                return HttpStatusCode.valueOf(404);
            }
        } catch (Exception ex){
            log.error("COULD NOT DELETE CUSTOMER WITH CUSTOMER_ID "+customerId,ex);
            return HttpStatusCode.valueOf(500);
        }
    }
}