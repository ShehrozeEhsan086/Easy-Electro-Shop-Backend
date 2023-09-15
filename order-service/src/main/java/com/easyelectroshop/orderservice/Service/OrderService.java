package com.easyelectroshop.orderservice.Service;

import com.easyelectroshop.orderservice.Model.OrderEntity;
import com.easyelectroshop.orderservice.Model.OrderContent;
import com.easyelectroshop.orderservice.Respository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

@Service
@Slf4j
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    WebClient.Builder webClientBuilder;

    public ResponseEntity<HttpStatusCode> saveOrder(OrderEntity orderEntity){
      try{
          log.info("ADDING ORDER FOR CUSTOMER WITH CUSTOMER_ID "+ orderEntity.getCustomerId());
          if( orderEntity.getTotalOrderPrice() == 0.0){
              double totalContentPrice = orderEntity.getTotalContentPrice();
              double shippingCost = orderEntity.getShippingCost();
              orderEntity.setTotalOrderPrice(totalContentPrice+shippingCost);
          }
          orderEntity.setCreatedAt(LocalDate.now());
          if(orderEntity.isOnlinePayment()){
              orderEntity.setOrderStatus("confirmed");
          } else {
              orderEntity.setOrderStatus("pending");
          }
          orderRepository.save(orderEntity);
          webClientBuilder.build()
                  .put()
                  .uri("http://customer-service/api/v1/customer/add-order-info/"+ orderEntity.getCustomerId()+"/"+ orderEntity.getTotalOrderPrice())
                  .retrieve()
                  .toBodilessEntity()
                  .flatMap(response -> Mono.just(response.getStatusCode()))
                  .block();

          log.info("SUCCESSFULLY ADDED ORDER FOR CUSTOMER WITH CUSTOMER_ID "+ orderEntity.getCustomerId());
          return ResponseEntity.ok().build();
      } catch (Exception ex){
          log.error("ERROR ADDING ORDER FOR CUSTOMER WITH CUSTOMER_ID "+ orderEntity.getCustomerId(),ex);
          return ResponseEntity.internalServerError().build();
      }
    }

    public ResponseEntity<HttpStatusCode> changeOrderStatus(long orderId,String status){
        log.info("CHANGING ORDER INFO FOR ORDER WITH ORDER_ID "+orderId);
        try{
            Optional<OrderEntity> order = orderRepository.findById(orderId);
            if(order.isPresent()){
                 order.get().setOrderStatus(status);
                 orderRepository.save(order.get());
                 log.info("ORDER WITH ORDER_ID "+orderId+" STATUS CHANGED TO "+status);
                 return ResponseEntity.ok().build();
            } else {
                log.error("ORDER WITH ORDER_ID "+orderId+" NOT FOUND");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex){
            log.error("ERROR CHANGING ORDER INFO FOR ORDER WITH ORDER_ID "+orderId,ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<HttpStatusCode> deleteOrder(long orderId){
        log.info("DELETING ORDER WITH ORDER_ID "+orderId);
        try{
            Optional<OrderEntity> order = orderRepository.findById(orderId);
            if(order.isPresent()){
                if(order.get().getOrderStatus().equals("pending") || order.get().getOrderStatus().equals("processing") || order.get().getOrderStatus().equals("confirmed")){
                    log.info("ORDER ELIGIBLE FOR DELETION, DELETING ORDER FOR ORDER_ID "+orderId);
                    for (OrderContent orderContent : order.get().getOrderContent()) {
                        webClientBuilder.build()
                                .put()
                                .uri("http://product-service/api/v1/product/increase-product-stock/"+orderContent.getProductId()+"/"+orderContent.getQuantity())
                                .retrieve()
                                .toBodilessEntity()
                                .flatMap(response -> Mono.just(response.getStatusCode()))
                                .block();
                    }
                    webClientBuilder.build()
                            .put()
                            .uri("http://customer-service/api/v1/customer/remove-order-info/"+order.get().getCustomerId()+"/"+order.get().getTotalOrderPrice())
                            .retrieve()
                            .toBodilessEntity()
                            .flatMap(response -> Mono.just(response.getStatusCode()))
                            .block();
                    orderRepository.delete(order.get());
                    log.info("SUCCESSFULLY DELETE ORDER");
                    return ResponseEntity.ok().build();
                } else {
                    log.error("ORDER NOT ELIGIBLE FOR DELETION");
                    return ResponseEntity.status(HttpStatusCode.valueOf(406)).build();
                }
            } else {
                log.error("ORDER WITH ORDER_ID "+orderId+" NOT FOUND");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex){
            log.error("ERROR DELETING ORDER WITH ORDER_ID "+orderId,ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<List<OrderEntity>> getAllOrders(String sortBy, int pageSize, int pageNumber){
        log.info("GETTING ALL ORDERS");
        try{
            return ResponseEntity.ok(orderRepository.findAllPaginated(sortBy,pageSize,pageNumber));
        } catch (Exception ex){
            log.error("ERROR GETTING ALL ORDERS");
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<HttpStatusCode> addShippingNumber(long orderId,String shippingNumber){
        log.info("ADDING SHIPPING NUMBER "+shippingNumber+" TO ORDER WITH ORDER_ID");
        try{
            Optional<OrderEntity> order = orderRepository.findById(orderId);
            if(order.isPresent()){
                order.get().setShippingTrackingNumber(shippingNumber);
                orderRepository.save(order.get());
                log.info("SUCCESSFULLY ADDED SHIPPING NUMBER "+shippingNumber+" TO ORDER WITH ORDER_ID");
                return ResponseEntity.ok().build();
            } else {
                log.error("ORDER WITH ORDER_ID "+orderId+" NOT FOUND");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex){
            log.error("ERROR ADDING SHIPPING NUMBER "+shippingNumber+" TO ORDER WITH ORDER_ID",ex);
            return ResponseEntity.internalServerError().build();
        }
    }

}