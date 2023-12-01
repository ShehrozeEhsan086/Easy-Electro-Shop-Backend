package com.example.chat.controller;

import com.example.chat.Repo.MessageRepo;
import com.example.chat.model.Message;
import com.example.chat.model.MessageEntity;
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

    @Autowired
    MessageRepo messageRepo;

    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    public Message receiveMessage(@Payload Message message){
        return message;
    }

    @MessageMapping("/private-message-from-admin")
    public Message recMessage(@Payload Message message){
        log.info("ADMIN SENDING PRIVATE TO CUSTOMER WITH CUSTOMER_NAME "+message.getReceiverName());
        simpMessagingTemplate.convertAndSendToUser(message.getReceiverName(),"/private",message);
        MessageEntity msg = new MessageEntity();
        msg.setType("private");
        msg.setMessage(message.getMessage());
        msg.setReceiverName(message.getReceiverName());
        msg.setSenderName(message.getSenderName());
        msg.setStatus(message.getStatus());
        msg.setDate(message.getDate());
        messageRepo.save(msg);
        return message;
    }

    @MessageMapping("/private-message-to-admin")
    public Message reccMessage(@Payload Message message){
        simpMessagingTemplate.convertAndSendToUser("Admin","/private",message);
        MessageEntity msg = new MessageEntity();
        msg.setType("private");
        msg.setMessage(message.getMessage());
        msg.setReceiverName("Admin");
        msg.setSenderName(message.getSenderName());
        msg.setStatus(message.getStatus());
        msg.setDate(message.getDate());
        messageRepo.save(msg);
        return message;
    }
}
