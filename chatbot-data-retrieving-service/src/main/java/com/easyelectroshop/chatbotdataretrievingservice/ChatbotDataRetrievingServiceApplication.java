package com.easyelectroshop.chatbotdataretrievingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ChatbotDataRetrievingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatbotDataRetrievingServiceApplication.class, args);
    }

}
