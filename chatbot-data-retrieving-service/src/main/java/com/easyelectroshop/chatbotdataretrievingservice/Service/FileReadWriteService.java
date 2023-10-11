package com.easyelectroshop.chatbotdataretrievingservice.Service;

import com.easyelectroshop.chatbotdataretrievingservice.Model.Data;
import com.easyelectroshop.chatbotdataretrievingservice.Model.Intents;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Service
@Slf4j
public class FileReadWriteService {

    @Scheduled(fixedRate = 10000)
    public void readWriteToFile(){
        try{
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = new FileInputStream(new File("C:/Users/shehr/OneDrive/Desktop/chatbot-service/intents.json"));
            TypeReference<Data> typeReference = new TypeReference<Data>() {};
            Data data = mapper.readValue(inputStream,typeReference);
            log.info(data.toString());

        } catch (FileNotFoundException fileNotFoundException){
            log.error("ERROR FILE NOT FOUND",fileNotFoundException);
        } catch (JsonParseException jsonParseException){
            log.error("ERROR PARSING JSON DATA FROM FILE",jsonParseException);
        } catch (Exception ex){
            log.error("ERROR",ex);
        }
    }
}