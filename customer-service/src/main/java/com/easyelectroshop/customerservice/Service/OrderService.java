package com.easyelectroshop.customerservice.Service;

import com.easyelectroshop.customerservice.DTO.Order.OrderEntity;
import com.easyelectroshop.customerservice.DTO.OrderGetAllResponse.OrderGetAllResponseEntity;
import com.easyelectroshop.customerservice.DTO.OrderGetByIdResponse.OrderSingleResponseEntity;
import com.easyelectroshop.customerservice.DTO.RatedOrderDTO.RatedOrder;
import com.easyelectroshop.customerservice.DTO.RatedOrdersResponse.RatedOrderResponseDTO;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class OrderService {

    @Autowired
    WebClient.Builder webClientBuilder;

    public ResponseEntity<OrderEntity> saveOrder(OrderEntity orderEntity){
        log.info("CALLING ORDER SERVICE TO SAVE ORDER FOR CUSTOMER WITH CUSTOMER_ID "+orderEntity.customerId());
        try{
            return webClientBuilder.build()
                    .post()
                    .uri("http://order-service/api/v1/order-service/add-order")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(orderEntity))
                    .retrieve()
                    .toEntity(OrderEntity.class)
                    .block();
        } catch (Exception ex){
            log.error("ERROR CALLING ORDER SERVICE TO SAVE ORDER FOR CUSTOMER WITH CUSTOMER_ID "+orderEntity.customerId(),ex);
            return ResponseEntity.internalServerError().build();
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

    public List<OrderGetAllResponseEntity> getAllOrders(String sortBy, int pageSize, int pageNumber) {
        log.info("CALLING ORDER SERVICE TO GET ALL ORDERS");
        try{
            return webClientBuilder.build()
                    .get()
                    .uri("http://order-service/api/v1/order-service/get-all?pageNumber="+pageNumber+"&pageSize="+pageSize+"&sort="+sortBy)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntityList(OrderGetAllResponseEntity.class)
                    .block()
                    .getBody();
        } catch (Exception ex){
            log.error("ERROR CALLING ORDER SERVICE TO GET ALL ORDERS",ex);
            return null;
        }
    }

    public List<RatedOrderResponseDTO> getAllOrdersByCustomerId(UUID customerId, String sortBy, int pageSize, int pageNumber) {
        log.info("CALLING ORDER SERVICE TO GET ALL ORDERS FOR CUSTOMER WITH CUSTOMER_ID "+customerId);
        try{
            List<OrderGetAllResponseEntity> orderGetAllResponseEntity = webClientBuilder.build()
                    .get()
                    .uri("http://order-service/api/v1/order-service/get-all-by-customer-id/"+customerId+"?pageNumber="+pageNumber+"&pageSize="+pageSize+"&sort="+sortBy)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntityList(OrderGetAllResponseEntity.class)
                    .block()
                    .getBody();

            List<RatedOrderResponseDTO> ratedOrdersResponse = new ArrayList<>();

            for(int i=0;i<orderGetAllResponseEntity.size();i++){

                RatedOrder ratedOrder = webClientBuilder.build()
                        .get()
                        .uri("http://rating-service/api/v1/rating-service/get-rating-by-orderid-customerid/"+orderGetAllResponseEntity.get(i).orderId()+"/"+customerId)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .toEntity(RatedOrder.class)
                        .block()
                        .getBody();

                if (ratedOrder == null){
                    // Current Order has not been rated yet!
                    RatedOrderResponseDTO ratedOrderResponseDTO = new RatedOrderResponseDTO(
                            orderGetAllResponseEntity.get(i).orderId(),
                            orderGetAllResponseEntity.get(i).productNames(),
                            orderGetAllResponseEntity.get(i).customerName(),
                            orderGetAllResponseEntity.get(i).totalPrice(),
                            orderGetAllResponseEntity.get(i).createdAt(),
                            orderGetAllResponseEntity.get(i).orderStatus(),
                            orderGetAllResponseEntity.get(i).shippingNumber(),
                            -1);
                    ratedOrdersResponse.add(ratedOrderResponseDTO);
                } else {
                    RatedOrderResponseDTO ratedOrderResponseDTO = new RatedOrderResponseDTO(
                            orderGetAllResponseEntity.get(i).orderId(),
                            orderGetAllResponseEntity.get(i).productNames(),
                            orderGetAllResponseEntity.get(i).customerName(),
                            orderGetAllResponseEntity.get(i).totalPrice(),
                            orderGetAllResponseEntity.get(i).createdAt(),
                            orderGetAllResponseEntity.get(i).orderStatus(),
                            orderGetAllResponseEntity.get(i).shippingNumber(),
                            ratedOrder.ratingValue());
                    ratedOrdersResponse.add(ratedOrderResponseDTO);
                }
            }

            return ratedOrdersResponse;

        } catch (Exception ex){
            log.error("ERROR CALLING ORDER SERVICE TO GET ALL ORDERS FOR CUSTOMER WITH CUSTOMER_ID "+customerId,ex);
            return null;
        }
    }

    public OrderSingleResponseEntity getOrderById(long orderId){
        log.info("CALLING ORDER SERVICE TO GET ORDER WITH ORDER_ID "+orderId);
        try{
            return webClientBuilder.build()
                    .get()
                    .uri("http://order-service/api/v1/order-service/get-order-by-id/"+orderId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(OrderSingleResponseEntity.class)
                    .block()
                    .getBody();
        } catch (WebClientResponseException.NotFound notFound){
            log.error("ORDER WITH ORDER_ID "+orderId+" NOT FOUND!");
            return null;
        } catch (Exception ex){
            log.error("ERROR CALLING ORDER SERVICE TO GET ORDER WITH ORDER_ID "+orderId,ex);
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

    public ResponseEntity<Long> getTotalOrdersCount() {
        log.info("CALLING ORDER SERVICE TO GET ALL ORDERS COUNT");
        try{
            return webClientBuilder.build()
                    .get()
                    .uri("http://order-service/api/v1/order-service/get-all-orders-count")
                    .retrieve()
                    .toEntity(Long.class)
                    .block();
        } catch (Exception ex){
            log.error("ERROR CALLING ORDER SERVICE TO GET ALL ORDERS COUNT",ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<Long> getPendingOrdersCount() {
        log.info("CALLING ORDER SERVICE TO GET ALL PENDING ORDERS COUNT");
        try{
            return webClientBuilder.build()
                    .get()
                    .uri("http://order-service/api/v1/order-service/get-pending-orders-count")
                    .retrieve()
                    .toEntity(Long.class)
                    .block();
        } catch (Exception ex){
            log.error("ERROR CALLING ORDER SERVICE TO GET ALL PENDING ORDERS COUNT",ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<Long> getConfirmedOrdersCount() {
        log.info("CALLING ORDER SERVICE TO GET ALL CONFIRMED ORDERS COUNT");
        try{
            return webClientBuilder.build()
                    .get()
                    .uri("http://order-service/api/v1/order-service/get-confirmed-orders-count")
                    .retrieve()
                    .toEntity(Long.class)
                    .block();
        } catch (Exception ex){
            log.error("ERROR CALLING ORDER SERVICE TO GET ALL CONFIRMED ORDERS COUNT",ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<Long> getReadyToShipOrdersCount() {
        log.info("CALLING ORDER SERVICE TO GET ALL READY TO SHIP ORDERS COUNT");
        try{
            return webClientBuilder.build()
                    .get()
                    .uri("http://order-service/api/v1/order-service/get-readytoship-orders-count")
                    .retrieve()
                    .toEntity(Long.class)
                    .block();
        } catch (Exception ex){
            log.error("ERROR CALLING ORDER SERVICE TO GET ALL READY TO SHIP ORDERS COUNT",ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<Long> getInTransitOrdersCount() {
        log.info("CALLING ORDER SERVICE TO GET ALL IN TRANSIT ORDERS COUNT");
        try{
            return webClientBuilder.build()
                    .get()
                    .uri("http://order-service/api/v1/order-service/get-intransit-orders-count")
                    .retrieve()
                    .toEntity(Long.class)
                    .block();
        } catch (Exception ex){
            log.error("ERROR CALLING ORDER SERVICE TO GET ALL IN TRANSIT ORDERS COUNT",ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<Long> getOrderCountForCustomer(UUID customerId) {
        log.info("CALLING ORDER SERVICE TO GET ALL ORDERS COUNT FOR CUSTOMER WITH CUSTOMER_ID "+customerId);
        try{
            return webClientBuilder.build()
                    .get()
                    .uri("http://order-service/api/v1/order-service/get-order-count-by-customer-id/"+customerId)
                    .retrieve()
                    .toEntity(Long.class)
                    .block();
        } catch (Exception ex){
            log.error("ERROR CALLING ORDER SERVICE TO GET ALL ORDERS COUNT FOR CUSTOMER WITH CUSTOMER_ID "+customerId,ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<Long> getDeliveredOrdersCount() {
        log.info("CALLING ORDER SERVICE TO GET DELIVERED ORDERS COUNT");
        try{
            return webClientBuilder.build()
                    .get()
                    .uri("http://order-service/api/v1/order-service/get-delivered-orders-count")
                    .retrieve()
                    .toEntity(Long.class)
                    .block();
        } catch (Exception ex){
            log.error("ERROR CALLING ORDER SERVICE TO GET ALL DELIVERED ORDERS COUNT",ex);
            return ResponseEntity.internalServerError().build();
        }
    }
}