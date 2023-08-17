package com.easyelectroshop.customerservice.Service;

import com.easyelectroshop.customerservice.DTO.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CustomerService {

    @Autowired
    WebClient.Builder webClientBuilder;

    // ----------------  SERVICE FOR CUSTOMER MANAGEMENT SERVICE [[START]] --------------------


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
        } catch (Exception ex){
            log.error("ERROR WHILE CALLING CUSTOMER MANAGEMENT SERVICE TO ADD CUSTOMER WITH CUSTOMER_USERNAME "+customer.userName(), ex);
            return HttpStatusCode.valueOf(500);
        }
    }


}