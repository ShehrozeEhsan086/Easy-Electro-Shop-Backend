package com.easyelectroshop.analyticsservice.Service;

import com.easyelectroshop.analyticsservice.DTO.ServiceStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

}