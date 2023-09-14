package com.easyelectroshop.ordertrackingservice.Controller;

import com.easyelectroshop.ordertrackingservice.Service.OrderTrackingService;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/order-tracking-service")
public class OrderTrackingController {

    @Autowired
    OrderTrackingService orderTrackingService;

    @GetMapping("/get-tracking-info/{trackingNumber}")
    public String getTrackingInfo(@PathVariable String trackingNumber){
        return orderTrackingService.getTrackingInfo(trackingNumber);
    }

}
