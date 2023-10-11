package com.example.chat.controller;

import com.example.chat.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class ChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    public Message receiveMessage(@Payload Message message){
        return message;
    }

    @MessageMapping("/private-message-from-admin")
    public Message recMessage(@Payload Message message){
        log.info("ADMIN SENDING PRIVATE TO CUSTOMER WITH CUSTOMER_NAME "+message.getReceiverName());
        simpMessagingTemplate.convertAndSendToUser(message.getReceiverName(),"/private",message);
        System.out.println(message.toString());
        return message;
    }

    @MessageMapping("/private-message-to-admin")
    public Message reccMessage(@Payload Message message){
        simpMessagingTemplate.convertAndSendToUser("Admin","/private",message);
        System.out.println(message.toString());
        return message;
    }
}
