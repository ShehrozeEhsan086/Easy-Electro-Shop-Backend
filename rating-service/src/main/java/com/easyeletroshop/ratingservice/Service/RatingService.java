package com.easyeletroshop.ratingservice.Service;

import com.easyeletroshop.ratingservice.DTO.OrderDTO.OrderEntity;
import com.easyeletroshop.ratingservice.DTO.RatedOrderDTO.RatedOrder;
import com.easyeletroshop.ratingservice.Model.Rating;
import com.easyeletroshop.ratingservice.Repository.RatingServiceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class RatingService {

    private final RatingServiceRepository ratingServiceRepository;

    private final WebClient.Builder webClientBuilder;


    public RatingService(RatingServiceRepository ratingServiceRepository, WebClient.Builder webClientBuilder) {
        this.ratingServiceRepository = ratingServiceRepository;
        this.webClientBuilder = webClientBuilder;
    }


    public ResponseEntity<HttpStatusCode> addRatingToOrder(RatedOrder ratedOrder){
        log.info("ADDING RATING FOR ORDER WITH ORDER_ID "+ratedOrder.orderId());
        try{
            OrderEntity orderObject = webClientBuilder.build()
                    .get()
                    .uri("http://order-service/api/v1/order-service/get-order-by-id-original-object/"+ratedOrder.orderId())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(OrderEntity.class)
                    .block()
                    .getBody();

            for(int i=0;i<orderObject.orderContent().size();i++){
                Rating rating = new Rating();
                rating.setRatingValue(ratedOrder.ratingValue());
                rating.setOrderId(ratedOrder.orderId());
                rating.setCustomerId(ratedOrder.customerId());
                rating.setProductId(orderObject.orderContent().get(i).productId());
                ratingServiceRepository.save(rating);
            }

            return ResponseEntity.ok().build();

        } catch (Exception ex){
            log.error("ERROR ADDING RATING FOR ORDER WITH ORDER_ID "+ratedOrder.orderId(),ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<RatedOrder> getRatingByOrderIdAndCustomerId(long orderId, UUID customerId) {
        log.info("GETTING RATING FOR ORDER WITH ORDER_ID "+orderId+" FOR CUSTOMER WITH CUSTOMER_ID "+customerId);
        try{
            Rating rating = ratingServiceRepository.findByCustomerIdAndOrderIdLimitOne(customerId,orderId);
            if (rating == null){
                log.warn("NOT DATA FOUND");
                return ResponseEntity.noContent().build();
            } else {
                log.info("SUCCESSFULLY FOUND DATA");
                return ResponseEntity.ok(new RatedOrder(rating.getCustomerId(),rating.getOrderId(),rating.getRatingValue()));
            }
        } catch (Exception ex){
            log.error("ERROR GETTING RATING FOR ORDER WITH ORDER_ID "+orderId+" FOR CUSTOMER WITH CUSTOMER_ID "+customerId,ex);
            return ResponseEntity.internalServerError().build();
        }
    }
}
