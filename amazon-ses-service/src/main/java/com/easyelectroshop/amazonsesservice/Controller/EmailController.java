package com.easyelectroshop.amazonsesservice.Controller;

import com.easyelectroshop.amazonsesservice.Model.Coupon.Coupon;
import com.easyelectroshop.amazonsesservice.Model.Order.OrderEntity;
import com.easyelectroshop.amazonsesservice.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/amazon-ses-service")
public class EmailController {

    @Autowired
    EmailService emailService;

    @PostMapping("/send-order-email")
    public ResponseEntity<HttpStatusCode> sendOrderEmail(@RequestBody OrderEntity order){
        emailService.sendOrderEmail(order);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send-coupon-email")
    public ResponseEntity<HttpStatusCode> sendCouponEmail(@RequestBody Coupon coupon){
        emailService.sendCouponEmail(coupon);
        return ResponseEntity.ok().build();
    }


}