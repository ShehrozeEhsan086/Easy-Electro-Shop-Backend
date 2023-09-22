package com.easyelectroshop.customerservice.Service;

import com.easyelectroshop.customerservice.DTO.OrderGetAllResponse.OrderGetAllResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@Slf4j
public class OrderTrackingService {

    @Autowired
    WebClient.Builder webClientBuilder;

    public ResponseEntity<String> getTrackingInfo(String trackingNumber){
        try{
            return webClientBuilder.build()
                    .get()
                    .uri("http://order-tracking-service/api/v1/order-tracking-service/get-tracking-info/"+trackingNumber)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(String.class)
                    .block();
        } catch (WebClientResponseException.NotFound notFound){
            log.error("DATA NOT FOUND FOR SHIPMENT WITH TRACKING_NUMBER "+trackingNumber);
            return ResponseEntity.notFound().build();
        }
        catch (Exception ex){
            log.error("ERROR CALLING ORDER TRACKING SERVICE TO GET INFO OF SHIPMENT WITH TRACKING_ID "+trackingNumber);
            return ResponseEntity.internalServerError().build();
        }
    }

}
