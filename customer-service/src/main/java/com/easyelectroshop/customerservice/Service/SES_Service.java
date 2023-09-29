package com.easyelectroshop.customerservice.Service;

import com.easyelectroshop.customerservice.DTO.Order.OrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class SES_Service {

    @Autowired
    WebClient.Builder webClientBuilder;

    public ResponseEntity<HttpStatusCode> sendOrderEmail(OrderEntity order){
        log.info("CALLING AMAZON SES SERVICE TO SEND EMAIL FOR ORDER WITH ORDER_ID "+order.orderId());
        try{
            webClientBuilder.build()
                    .post()
                    .uri("http://amazon-ses-service/api/v1/amazon-ses-service/send-order-email")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(order))
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> Mono.just(response.getStatusCode()))
                    .block();
            return ResponseEntity.ok().build();
        } catch (Exception ex){
            log.error("ERROR CALLING AMAZON SES SERVICE TO SEND EMAIL FOR ORDER WITH ORDER_ID "+order.orderId(),ex);
            return ResponseEntity.internalServerError().build();
        }
    }


}
