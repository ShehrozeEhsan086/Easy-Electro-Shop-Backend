package com.easyelectroshop.customerservice.Service;

import com.easyelectroshop.customerservice.DTO.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
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
        log.info("CALLING CUSTOMER MANAGEMENT SERVICE TO ADD CUSTOMER WITH CUSTOMER_USERNAME "+ customer.userName());
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
            log.error("ERROR WHILE CALLING CUSTOMER MANAGEMENT SERVICE TO ADD CUSTOMER WITH CUSTOMER_USERNAME "+customer.userName(), ex);
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

}