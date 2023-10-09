package com.easyelectroshop.customermanagementservice.Service;

import com.easyelectroshop.customermanagementservice.DTO.CustomerDTO;
import com.easyelectroshop.customermanagementservice.Model.Customer;
import com.easyelectroshop.customermanagementservice.Model.PaymentMethod;
import com.easyelectroshop.customermanagementservice.Repository.CustomerManagementRepository;
import com.easyelectroshop.customermanagementservice.Repository.PaymentMethodRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class CustomerManagementService {

    @Autowired
    CustomerManagementRepository customerManagementRepository;

    @Autowired
    PaymentMethodRepository paymentMethodRepository;

    private boolean validateCustomerProfileCompleteness(Customer customer){
        return customer.getFullName() != null
                && customer.getEmail() != null
                && customer.getPhoneNumber() != null
                && customer.getGender() != null;
    }

    public List<CustomerDTO> getAllCustomers(int pageNumber, int pageSize, String sortBy){
        log.info("GETTING ALL CUSTOMERS");
        try{
            if(pageSize == -1){
                pageSize = Integer.MAX_VALUE;
            }
            Page<Customer> retrievedCustomer = customerManagementRepository.findAll(PageRequest.of(pageNumber,pageSize, Sort.by(sortBy).descending()));
            List<Customer> customers = retrievedCustomer.getContent();
            List<CustomerDTO> customerDTOS = new ArrayList<>();
            for(Customer customer : customers){
                CustomerDTO customerDTO = new CustomerDTO(customer.getCustomerId(),customer.getFullName(),
                        customer.getEmail(),customer.getPhoneNumber(),customer.getGender(),customer.getDateOfBirth(),customer.getAddress(),
                        customer.isProfileComplete(),customer.isBlocked(),customer.isActive(),customer.getTotalOrders(),
                        customer.getTotalOrdersAmount());
                customerDTOS.add(customerDTO);
            }
            log.info("SUCCESSFULLY RETRIEVED ALL CUSTOMERS");
            return customerDTOS;
        } catch (Exception ex){
            log.error("COULD NOT RETRIEVE ALL CUSTOMERS",ex);
            return null;
        }
    }

    public CustomerDTO getCustomerById(UUID customerId){
        log.info("GETTING CUSTOMER WITH CUSTOMER_ID "+customerId);
        Optional<Customer> customerOpt = customerManagementRepository.findById(customerId);
        if(customerOpt.isPresent()){
            Customer customer = customerOpt.get();
            CustomerDTO customerDTO = new CustomerDTO(customer.getCustomerId(),customer.getFullName(),
                    customer.getEmail(),customer.getPhoneNumber(),customer.getGender(),customer.getDateOfBirth(),customer.getAddress(),
                    customer.isProfileComplete(),customer.isBlocked(),customer.isActive(),customer.getTotalOrders(),
                    customer.getTotalOrdersAmount());
            log.info("SUCCESSFULLY RETRIEVED CUSTOMER WITH CUSTOMER_ID "+customerId);
            return customerDTO;
        } else {
            log.error("COULD NOT FIND CUSTOMER WITH CUSTOMER_ID "+customerId);
            return null;
        }
    }

    public ResponseEntity<CustomerDTO> getCustomerByEmail(String customerEmail){
        log.info("GETTING CUSTOMER WITH CUSTOMER_EMAIL "+customerEmail);
        try{
            Optional<Customer> customerOpt = customerManagementRepository.findByEmail(customerEmail);
            if(customerOpt.isPresent()){
                Customer customer = customerOpt.get();
                CustomerDTO customerDTO = new CustomerDTO(customer.getCustomerId(),customer.getFullName(),
                        customer.getEmail(),customer.getPhoneNumber(),customer.getGender(),customer.getDateOfBirth(),customer.getAddress(),
                        customer.isProfileComplete(),customer.isBlocked(),customer.isActive(),customer.getTotalOrders(),
                        customer.getTotalOrdersAmount());
                log.info("SUCCESSFULLY RETRIEVED CUSTOMER WITH CUSTOMER_EMAIL "+customerEmail);
                return ResponseEntity.ok(customerDTO);
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
        log.info("ADDING NEW CUSTOMER WITH EMAIL "+customer.getEmail());
        try{
            Optional<Customer> tempCustomer = customerManagementRepository.findByEmail(customer.getEmail());
            if( tempCustomer.isPresent()){
                return HttpStatusCode.valueOf(409);
            } else{
                customerManagementRepository.save(customer);
                log.info("SUCCESSFULLY ADDED CUSTOMER WITH CUSTOMER EMAIL "+customer.getEmail());
                return HttpStatusCode.valueOf(201);
            }
        } catch (Exception ex){
            log.error("ERROR WHILE ADDING CUSTOMER WITH EMAIL "+customer.getEmail(),ex);
            return HttpStatusCode.valueOf(500);
        }
    }

    public HttpStatusCode increaseOrderCountAmountOfCustomer(UUID customerId,double amount){
        log.info("ADDING 1 TO ORDER COUNT AND INCREASING TOTAL ORDERS AMOUNT BY RS."+amount+" OF CUSTOMER WITH CUSTOMER_ID "+customerId);
        try{
            Optional<Customer> customer = customerManagementRepository.findById(customerId);
            if(customer.isPresent()){
                int totalOrders = customer.get().getTotalOrders();
                totalOrders++;
                customer.get().setTotalOrders(totalOrders);
                double currentAmount = customer.get().getTotalOrdersAmount();
                double newAmount = currentAmount + amount;
                customer.get().setTotalOrdersAmount(newAmount);
                customerManagementRepository.save(customer.get());
                return HttpStatusCode.valueOf(200);
            } else {
                log.error("CUSTOMER WITH CUSTOMER_ID "+customer+" NOT FOUND!");
                return HttpStatusCode.valueOf(404);
            }
        } catch (Exception ex){
            log.error("ERROR WHILE ADDING 1 TO ORDER COUNT AND INCREASING TOTAL ORDERS AMOUNT BY RS."+amount+" OF CUSTOMER WITH CUSTOMER_ID "+customerId,ex);
            return HttpStatusCode.valueOf(500);
        }
    }
    public HttpStatusCode decreaseOrderCountAmountOfCustomer(UUID customerId,double amount){
        log.info("REMOVING 1 TO ORDER COUNT AND REDUCING TOTAL ORDERS AMOUNT BY RS."+amount+" OF CUSTOMER WITH CUSTOMER_ID "+customerId);
        try{
            Optional<Customer> customer = customerManagementRepository.findById(customerId);
            if(customer.isPresent()){
                int totalOrders = customer.get().getTotalOrders();
                if(totalOrders - 1 >= 0){
                    totalOrders--;
                    customer.get().setTotalOrders(totalOrders);
                    double currentAmount = customer.get().getTotalOrdersAmount();
                    currentAmount -= amount;
                    customer.get().setTotalOrdersAmount(currentAmount);
                    customerManagementRepository.save(customer.get());
                    return HttpStatusCode.valueOf(200);
                } else {
                    return HttpStatusCode.valueOf(406);
                }
            } else {
                log.error("CUSTOMER WITH CUSTOMER_ID "+customer+" NOT FOUND!");
                return HttpStatusCode.valueOf(404);
            }
        } catch (Exception ex){
            log.error("ERROR WHILE REMOVING 1 TO ORDER COUNT AND REDUCING TOTAL ORDERS AMOUNT BY RS."+amount+" OF CUSTOMER WITH CUSTOMER_ID "+customerId,ex);
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
                        return HttpStatusCode.valueOf(204);
                    }
                } else {
                    log.error("ERROR COMPLETING PROFILE FOR CUSTOMER WITH CUSTOMER_ID "+ customer.getCustomerId() +", EMAIL CANNOT BE CHANGED");
                    return HttpStatusCode.valueOf(204);
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
                        customer.setProfileComplete(true);
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

    public ResponseEntity<PaymentMethod> getCustomerPaymentMethod(UUID customerId){
        log.info("GETTING PAYMENT METHOD OF CUSTOMER WITH CUSTOMER_ID "+customerId);
        try{
            Optional<Customer> customer = customerManagementRepository.findById(customerId);
            if(customer.isPresent()){
                Optional<PaymentMethod> paymentMethod = paymentMethodRepository.findById(customer.get().getPaymentOption().getPaymentOptionId());
                if(paymentMethod.isPresent()){
                    return ResponseEntity.ok(paymentMethod.get());
                }else {
                    log.error("CUSTOMER WITH CUSTOMER_ID "+customerId+", HAS NO PAYMENT METHOD LINKED TO THEIR ACCOUNT!");
                    return ResponseEntity.notFound().build();
                }
            } else {
                log.error("CUSTOMER WITH CUSTOMER_ID "+customerId+", NOT FOUND!");
                return ResponseEntity.notFound().build();
            }
        }catch (Exception ex){
            log.error("ERROR GETTING PAYMENT METHOD OF CUSTOMER WITH CUSTOMER_ID "+customerId,ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<String> getCustomerNameById(UUID customerId){
        log.info("GETTING CUSTOMER NAME FOR CUSTOMER WITH CUSTOMER_ID "+customerId);
        try{
            String fullName = customerManagementRepository.findById(customerId).get().getFullName();
            log.info("SUCCESSFULLY RETRIEVED CUSTOMER_NAME "+fullName+" FOR CUSTOMER WITH CUSTOMER_ID "+customerId);
            return ResponseEntity.ok(fullName);
        } catch (NoSuchElementException noSuchElementException){
            log.error("ERROR GETTING CUSTOMER NAME FOR CUSTOMER WITH CUSTOMER_ID "+customerId+" FIELD NOT FOUND!");
            return ResponseEntity.notFound().build();
        }
        catch (Exception ex){
            log.error("ERROR GETTING CUSTOMER NAME FOR CUSTOMER WITH CUSTOMER_ID "+customerId,ex);
            return ResponseEntity.internalServerError().build();
        }
    }

}