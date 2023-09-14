package com.easyelectroshop.customerservice.Service;

import com.easyelectroshop.customerservice.DTO.Customer.Customer;
import com.easyelectroshop.customerservice.DTO.Order.OrderEntity;
import lombok.extern.java.Log;
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

import java.util.List;

@Service
@Slf4j
public class OrderService {

    @Autowired
    WebClient.Builder webClientBuilder;

    public HttpStatusCode saveOrder(OrderEntity orderEntity){
        log.info("CALLING ORDER SERVICE TO SAVE ORDER FOR CUSTOMER WITH CUSTOMER_ID "+orderEntity.customerId());
        try{
            return webClientBuilder.build()
                    .post()
                    .uri("http://order-service/api/v1/order-service/add-order")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(orderEntity))
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> Mono.just(response.getStatusCode()))
                    .block();
        } catch (Exception ex){
            log.error("ERROR CALLING ORDER SERVICE TO SAVE ORDER FOR CUSTOMER WITH CUSTOMER_ID "+orderEntity.customerId(),ex);
            return HttpStatusCode.valueOf(500);
        }
    }

    public HttpStatusCode changeOrderStatus(long orderId,String status){
        log.info("CALLING ORDER SERVICE TO CHANGE STATUS OF ORDER WITH ORDER_ID "+orderId+" TO "+status);
        try{
            return webClientBuilder.build()
                    .put()
                    .uri("http://order-service/api/v1/order-service/change-order-status/"+orderId+"/"+status)
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> Mono.just(response.getStatusCode()))
                    .block();
        } catch (WebClientResponseException.NotFound notFound){
            log.error("ORDER WITH ORDER_ID "+orderId+" NOT FOUND");
            return HttpStatusCode.valueOf(404);
        } catch (Exception ex){
            log.error("ERROR CALLING ORDER SERVICE TO CHANGE STATUS OF ORDER WITH ORDER_ID "+orderId+" TO "+status,ex);
            return HttpStatusCode.valueOf(500);
        }
    }

    public HttpStatusCode deleteOrder(long orderId){
        log.info("CALLING ORDER SERVICE TO DELETE ORDER FOR ORDER_ID "+orderId);
        try{
            return webClientBuilder.build()
                    .delete()
                    .uri("http://order-service/api/v1/order-service/delete-order/"+orderId)
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> Mono.just(response.getStatusCode()))
                    .block();
        } catch (WebClientResponseException.NotFound notFound){
            log.error("ORDER WITH ORDER_ID "+orderId+" NOT FOUND");
            return HttpStatusCode.valueOf(404);
        } catch (WebClientResponseException.NotAcceptable notAcceptable){
            log.error("ORDER NOT ELIGIBLE FOR DELETION");
            return HttpStatusCode.valueOf(406);
        } catch (Exception ex){
            log.error("ERROR CALLING ORDER SERVICE TO DELETE ORDER FOR ORDER_ID "+orderId,ex);
            return HttpStatusCode.valueOf(500);
        }
    }

    public List<OrderEntity> getAllOrders(String sortBy, int pageSize, int pageNumber) {
        log.info("CALLING ORDER SERVICE TO GET ALL ORDERS");
        try{
            return webClientBuilder.build()
                    .get()
                    .uri("http://order-service/api/v1/order-service/get-all?pageNumber="+pageNumber+"&pageSize="+pageSize+"&sort="+sortBy)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntityList(OrderEntity.class)
                    .block()
                    .getBody();
        } catch (Exception ex){
            log.error("ERROR CALLING ORDER SERVICE TO GET ALL ORDERS",ex);
            return null;
        }
    }

    public HttpStatusCode addShippingNumber(long orderId, String shippingNumber){
        log.info("CALLING ORDER SERVICE TO ADD SHIPPING NUMBER "+shippingNumber+" TO ORDER WITH ORDER_ID "+orderId);
        try{
            return webClientBuilder.build()
                    .post()
                    .uri("http://order-service/api/v1/order-service/add-tracking-number/"+orderId+"/"+shippingNumber)
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> Mono.just(response.getStatusCode()))
                    .block();
        } catch (WebClientResponseException.NotFound notFound){
            log.error("ORDER WITH ORDER_ID "+orderId+" NOT FOUND");
            return HttpStatusCode.valueOf(404);
        } catch (WebClientResponseException.ServiceUnavailable unavailable){
            log.error("ORDER SERVICE UNAVAILABLE");
            return HttpStatusCode.valueOf(503);
        } catch (Exception ex){
            log.error("INTERNAL SERVER ERROR WHILE CALLING ORDER SERVICE TO ADD SHIPPING NUMBER FOR ORDER WITH ORDER_ID "+orderId);
            return HttpStatusCode.valueOf(500);
        }
    }

    }