package com.easyelectroshop.customerservice.Service;

import com.easyelectroshop.customerservice.DTO.Customer.Customer;
import com.easyelectroshop.customerservice.DTO.Customer.PaymentMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.List;

@Service
@Slf4j
public class CustomerService {

    @Autowired
    WebClient.Builder webClientBuilder;

    public HttpStatusCode saveCustomer(Customer customer){
        log.info("CALLING CUSTOMER MANAGEMENT SERVICE TO ADD CUSTOMER WITH CUSTOMER_EMAIL "+ customer.email());
        try{
            return webClientBuilder.build()
                    .post()
                    .uri("http://customer-management-service/api/v1/customer-management/add-customer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(customer))
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> Mono.just(response.getStatusCode()))
                    .block();
        } catch (WebClientResponseException.Conflict conflict){
            log.error("CANNOT ADD CUSTOMER WITH EMAIL "+customer.email()+" CUSTOMER ALREADY EXISTS WITH GIVEN EMAIL!");
            return HttpStatusCode.valueOf(409);
        } catch (Exception ex){
            log.error("ERROR WHILE CALLING CUSTOMER MANAGEMENT SERVICE TO ADD CUSTOMER WITH CUSTOMER_EMAIL "+ customer.email(), ex);
            return HttpStatusCode.valueOf(500);
        }
    }

    public Customer getCustomerById(UUID customerId){
        log.info("CALLING CUSTOMER MANAGEMENT SERVICE TO GET CUSTOMER WITH CUSTOMER_ID "+ customerId);
        try{
            return webClientBuilder.build()
                    .get()
                    .uri("http://customer-management-service/api/v1/customer-management/get-customer-by-id/"+customerId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(Customer.class)
                    .block()
                    .getBody();
        } catch (Exception ex){
            log.error("ERROR WHILE CALLING CUSTOMER MANAGEMENT SERVICE TO GET CUSTOMER WITH CUSTOMER_ID "+customerId, ex);
            return null;
        }
    }

    public Customer getCustomerByEmail(String customerEmail){
        log.info("CALLING CUSTOMER MANAGEMENT SERVICE TO GET CUSTOMER WITH CUSTOMER_EMAIL "+ customerEmail);
        try{
            return webClientBuilder.build()
                    .get()
                    .uri("http://customer-management-service/api/v1/customer-management/get-customer-by-email/"+customerEmail)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(Customer.class)
                    .block()
                    .getBody();
        } catch (Exception ex){
            log.error("ERROR WHILE CALLING CUSTOMER MANAGEMENT SERVICE TO GET CUSTOMER WITH CUSTOMER_EMAIL "+customerEmail, ex);
            return null;
        }
    }

    public List<Customer> getAllCustomers(int pageNumber,int pageSize,String sortBy){
        log.info("CALLING CUSTOMER MANAGEMENT SERVICE TO GET ALL CUSTOMERS");
        try{
            return webClientBuilder.build()
                    .get()
                    .uri("http://customer-management-service/api/v1/customer-management/get-all?pageNumber="+pageNumber+"&pageSize="+pageSize+"&sort="+sortBy)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntityList(Customer.class)
                    .block()
                    .getBody();
        } catch (Exception ex){
            log.error("ERROR CALLING CUSTOMER MANAGEMENT SERVICE TO GET ALL CUSTOMERS",ex);
            return null;
        }
    }

    public Integer getAllCount(){
        log.info("CALLING CUSTOMER MANAGEMENT SERVICE TO GET NUMBER OF CUSTOMERS");
        try{
            return webClientBuilder.build()
                    .get()
                    .uri("http://customer-management-service/api/v1/customer-management/get-all-count")
                    .retrieve()
                    .bodyToMono(Integer.class)
                    .block();
        } catch (Exception ex){
            log.error("ERROR CALLING CUSTOMER MANAGEMENT SERVICE TO GET NUMBER OF CUSTOMERS",ex);
            return 0;
        }
    }

    public HttpStatusCode updateCustomer(Customer customer){
        log.info("CALLING CUSTOMER MANAGEMENT SERVICE TO UPDATE CUSTOMER WITH CUSTOMER_ID "+customer.customerId());
        try{
            return webClientBuilder.build()
                    .put()
                    .uri("http://customer-management-service/api/v1/customer-management/update-customer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(customer))
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> Mono.just(response.getStatusCode()))
                    .block();
        } catch (Exception ex){
            log.error("ERROR CALLING CUSTOMER MANAGEMENT SERVICE TO UPDATE CUSTOMER WITH CUSTOMER_ID "+customer.customerId(),ex);
            return HttpStatusCode.valueOf(500);
        }
    }

    public HttpStatusCode completeCustomerProfile(Customer customer){
        log.info("CALLING CUSTOMER MANAGEMENT SERVICE TO COMPLETE CUSTOMER PROFILE WITH CUSTOMER_ID "+customer.customerId());
        try{
            return webClientBuilder.build()
                    .put()
                    .uri("http://customer-management-service/api/v1/customer-management/complete-customer-profile")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(customer))
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> Mono.just(response.getStatusCode()))
                    .block();
        } catch (WebClientResponseException.NotAcceptable notAcceptable) {
            log.error("CANNOT COMPLETE PROFILE AS REQUIREMENTS WERE NOT MET!");
            return HttpStatusCode.valueOf(406);
        } catch (WebClientResponseException.Conflict conflict) {
            log.error("CANNOT COMPLETE PROFILE AS EMAIL WAS CANNOT BE CHANGES!");
            return HttpStatusCode.valueOf(409);
        } catch (WebClientResponseException.NotFound notFound) {
            log.error("CANNOT COMPLETE PROFILE AS NO PROFILE WAS FOUND!");
            return HttpStatusCode.valueOf(404);
        } catch (Exception ex){
            log.error("ERROR CALLING CUSTOMER MANAGEMENT SERVICE TO COMPLETE CUSTOMER PROFILE WITH CUSTOMER_ID "+customer.customerId(), ex);
            return HttpStatusCode.valueOf(500);
        }
    }

    public HttpStatusCode deleteCustomer(UUID customerId){
        log.info("CALLING CUSTOMER MANAGEMENT SERVICE TO DELETE CUSTOMER WITH CUSTOMER_ID "+customerId);
        try{
            return webClientBuilder.build()
                    .delete()
                    .uri("http://customer-management-service/api/v1/customer-management/delete-customer/"+customerId)
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> Mono.just(response.getStatusCode()))
                    .block();
        }  catch (WebClientResponseException.NotFound notFound){
            log.error("COULD NOT DELETE CUSTOMER WITH CUSTOMER_ID "+customerId+" CUSTOMER NOT FOUND!");
            return HttpStatusCode.valueOf(404);
        } catch (Exception ex){
            log.error("ERROR DELETING CUSTOMER WITH CUSTOMER_ID "+customerId,ex);
            return HttpStatusCode.valueOf(500);
        }
    }

    public PaymentMethod getCustomerPaymentMethod(UUID customerId){
        log.info("CALLING CUSTOMER MANAGEMENT SERVICE TO GET PAYMENT METHOD OF CUSTOMER WITH CUSTOMER_ID "+customerId);
        try{
            return webClientBuilder.build()
                    .get()
                    .uri("http://customer-management-service/api/v1/customer-management/get-customer-payment-method/"+customerId)
                    .retrieve()
                    .bodyToMono(PaymentMethod.class)
                    .block();
        } catch (WebClientResponseException.NotFound notFound){
            log.error("EITHER CUSTOMER NOT FOUND OR PAYMENT METHOD NOT FOUND FOR CUSTOMER WITH CUSTOMER_ID "+customerId);
            return null;
        } catch (Exception ex){
            log.error("ERROR CALLING CUSTOMER MANAGEMENT SERVICE TO GET PAYMENT METHOD OF CUSTOMER WITH CUSTOMER_ID "+customerId,ex);
            return null;
        }
    }

    public HttpStatusCode increaseOrderInfo(UUID customerId, double amount){
        log.info("CALLING CUSTOMER MANAGEMENT SERVICE TO ADD ORDER INFO TO CUSTOMER WITH CUSTOMER_ID "+customerId);
        try {
            return webClientBuilder.build()
                    .put()
                    .uri("http://customer-management-service/api/v1/customer-management/add-order-info/"+customerId+"/"+amount)
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> Mono.just(response.getStatusCode()))
                    .block();
        } catch (WebClientResponseException.NotFound notFound){
            log.error("CUSTOMER MANAGEMENT SERVICE RETURNED RETURNED 404 NOT FOUND FOR CUSTOMER WITH CUSTOMER_ID "+customerId);
            return HttpStatusCode.valueOf(404);
        } catch (Exception ex){
            log.error("ERROR CALLING CUSTOMER MANAGEMENT SERVICE TO ADD ORDER INFO TO CUSTOMER WITH CUSTOMER_ID "+customerId);
            return HttpStatusCode.valueOf(500);
        }
    }

    public HttpStatusCode decreaseOrderInfo(UUID customerId, double amount){
        log.info("CALLING CUSTOMER MANAGEMENT SERVICE TO REMOVE ORDER INFO TO CUSTOMER WITH CUSTOMER_ID "+customerId);
        try {
            return webClientBuilder.build()
                    .put()
                    .uri("http://customer-management-service/api/v1/customer-management/remove-order-info/"+customerId+"/"+amount)
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> Mono.just(response.getStatusCode()))
                    .block();
        } catch (WebClientResponseException.NotFound notFound){
            log.error("CUSTOMER MANAGEMENT SERVICE RETURNED RETURNED 404 NOT FOUND FOR CUSTOMER WITH CUSTOMER_ID "+customerId);
            return HttpStatusCode.valueOf(404);
        } catch (WebClientResponseException.NotAcceptable notAcceptable){
            log.error("REQUEST REJECTED BY CUSTOMER MANAGEMENT SERVICE ");
            return HttpStatusCode.valueOf(406);
        } catch (Exception ex){
            log.error("ERROR CALLING CUSTOMER MANAGEMENT SERVICE TO REMOVE ORDER INFO TO CUSTOMER WITH CUSTOMER_ID "+customerId);
            return HttpStatusCode.valueOf(500);
        }
    }


    public ResponseEntity<HttpStatusCode> blockCustomer(UUID customerId) {
        log.info("CALLING CUSTOMER MANAGEMENT SERVICE TO BLOCK CUSTOMER WITH CUSTOMER_ID "+customerId);
        try{
            return webClientBuilder.build()
                    .put()
                    .uri("http://customer-management-service/api/v1/customer-management/block-customer/"+customerId)
                    .retrieve()
                    .toEntity(HttpStatusCode.class)
                    .block();
        } catch (Exception ex){
            log.error("ERROR CALLING CUSTOMER MANAGEMENT SERVICE TO BLOCK CUSTOMER WITH CUSTOMER_ID "+customerId,ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<HttpStatusCode> unBlockCustomer(UUID customerId) {
        log.info("CALLING CUSTOMER MANAGEMENT SERVICE TO UNBLOCK CUSTOMER WITH CUSTOMER_ID "+customerId);
        try{
            return webClientBuilder.build()
                    .put()
                    .uri("http://customer-management-service/api/v1/customer-management/unblock-customer/"+customerId)
                    .retrieve()
                    .toEntity(HttpStatusCode.class)
                    .block();
        } catch (Exception ex){
            log.error("ERROR CALLING CUSTOMER MANAGEMENT SERVICE TO UNBLOCK CUSTOMER WITH CUSTOMER_ID "+customerId,ex);
            return ResponseEntity.internalServerError().build();
        }
    }
}