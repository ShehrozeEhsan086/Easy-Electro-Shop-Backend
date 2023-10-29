package com.easyelectroshop.analyticsservice.Service;

import com.easyelectroshop.analyticsservice.DTO.ServiceStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class AnalyticsService {

    @Autowired
    WebClient.Builder webClientBuilder;

    public ResponseEntity<ServiceStatus> getServiceStatus(String serviceName){
        log.info("CALLING "+serviceName+" TO GET SERVICE STATUS");
        try{
            Object orderServiceResponse = webClientBuilder.build()
                    .get()
                    .uri("http://"+serviceName+"/actuator/health")
                    .retrieve()
                    .toEntity(Object.class)
                    .block()
                    .getBody();

            ServiceStatus status = new ServiceStatus((String) PropertyUtils.getProperty(orderServiceResponse,"status"));
            log.info("RETURNED STATUS: "+status);
            return ResponseEntity.ok(status);
        } catch (Exception ex){
            log.error("ERROR", ex);
            ServiceStatus status = new ServiceStatus("DOWN");
            return ResponseEntity.ok(status);
        }
    }

    public ResponseEntity<String> getTotalInventoryPrice(){
        log.info("CALLING PRODUCT MANAGEMENT SERVICE TO GET TOTAL INVENTORY PRICE");
        try{
            String totalInventoryPrice = webClientBuilder.build()
                    .get()
                    .uri("http://product-management-service/api/v1/product-management/get-total-inventory-price")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(String.class)
                    .block()
                    .getBody();
            log.info("SUCCESSFULLY RETRIEVED TOTAL INVENTORY PRICE VALUE: "+totalInventoryPrice);
            return ResponseEntity.ok(totalInventoryPrice);
        } catch (Exception ex){
            log.error("ERROR ",ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<String> getTotalSoldPrice() {
        log.info("CALLING PRODUCT MANAGEMENT SERVICE TO GET TOTAL SALES AMOUNT");
        try{
            String totalSalesPrice = webClientBuilder.build()
                    .get()
                    .uri("http://order-service/api/v1/order-service/get-total-sales-price")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(String.class)
                    .block()
                    .getBody();
            log.info("SUCCESSFULLY RETRIEVED TOTAL SALES AMOUNT VALUE: "+totalSalesPrice);
            return ResponseEntity.ok(totalSalesPrice);
        } catch (Exception ex){
            log.error("ERROR ",ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<String> getTotalOnHoldAmount() {
        log.info("CALLING PRODUCT MANAGEMENT SERVICE TO GET TOTAL ON HOLD AMOUNT");
        try{
            String totalOnHoldPrice = webClientBuilder.build()
                    .get()
                    .uri("http://order-service/api/v1/order-service/get-total-amount-on-hold")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(String.class)
                    .block()
                    .getBody();
            log.info("SUCCESSFULLY RETRIEVED TOTAL ON HOLD AMOUNT VALUE: "+totalOnHoldPrice);
            return ResponseEntity.ok(totalOnHoldPrice);
        } catch (Exception ex){
            log.error("ERROR ",ex);
            return ResponseEntity.internalServerError().build();
        }
    }
}