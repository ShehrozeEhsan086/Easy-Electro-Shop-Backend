package com.easyelectroshop.analyticsservice.Controller;

import com.easyelectroshop.analyticsservice.DTO.ServiceStatus;
import com.easyelectroshop.analyticsservice.Service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics-service")
public class AnalyticsServiceController {

    @Autowired
    AnalyticsService analyticsService;

    @GetMapping("/get-service-status")
    public ResponseEntity<ServiceStatus> getServiceStatus(@RequestParam (value="serviceName",required = true) String serviceName){
        return analyticsService.getServiceStatus(serviceName);
    }

    @GetMapping("/get-total-inventory-price")
    public ResponseEntity<String> getTotalInventoryPrice(){
        return analyticsService.getTotalInventoryPrice();
    }

    @GetMapping("/get-total-sold-price")
    public ResponseEntity<String> getTotalSalesPrice(){
        return analyticsService.getTotalSoldPrice();
    }

    @GetMapping("/get-total-amount-on-hold")
    public ResponseEntity<String> getTotalOnHoldPrice(){
        return analyticsService.getTotalOnHoldAmount();
    }

}