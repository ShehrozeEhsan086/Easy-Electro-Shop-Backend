package com.easyelectroshop.amazonsesservice.Controller;

import com.easyelectroshop.amazonsesservice.Model.Order.OrderEntity;
import com.easyelectroshop.amazonsesservice.Service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/amazon-ses-service")
public class MailController {

    @Autowired
    MailService mailService;

    @PostMapping("/send-order-email")
    public ResponseEntity<HttpStatusCode> sendOrderEmail(@RequestBody OrderEntity order){
        return mailService.sendOrderEmail(order);
    }

}