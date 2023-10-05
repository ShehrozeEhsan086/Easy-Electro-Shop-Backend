package com.easyelectroshop.amazonsesservice.Controller;

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
        emailService.htmlSend(order);
        return ResponseEntity.ok().build();
    }


}