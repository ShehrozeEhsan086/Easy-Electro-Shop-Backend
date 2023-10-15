package com.easyelectroshop.chatbotdataretrievingservice.Service;

import com.easyelectroshop.chatbotdataretrievingservice.Model.IntentData;
import com.easyelectroshop.chatbotdataretrievingservice.Model.Intents;
import com.easyelectroshop.chatbotdataretrievingservice.ProductCategoryDTO.Category;
import com.easyelectroshop.chatbotdataretrievingservice.ProductDTO.Product;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

@Service
@Slf4j
public class FileReadWriteService {

    @Autowired
    WebClient.Builder webClientBuilder;

//    @Scheduled(fixedRate = 600000)
    @Scheduled(fixedRate = 3600000)
    public void readWriteToFile(){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Intents intents = objectMapper.readValue(new File("C:/Users/shehr/OneDrive/Desktop/chatbot-service/intents.json"), Intents.class);
            List<IntentData> intentList = intents.getIntents();
            for (IntentData intent : intentList) {
                System.out.println("Tag: " + intent.tag());
                System.out.println("Patterns: " + intent.patterns());
                System.out.println("Responses: " + intent.responses());
                System.out.println();
            }

            List<Category> categories = webClientBuilder.build()
                    .get()
                    .uri("http://product-category-management-service/api/v1/category/get-all")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntityList(Category.class)
                    .block()
                    .getBody();

            long electronicsCategory = -1;
            long homeAppliancesCategory = -1;

            String itemsResponse = "We have";
            for(int i=0;i<categories.size();i++){
                itemsResponse = itemsResponse + ", "+categories.get(i).categoryName();
                if(categories.get(i).categoryName().equals("Electronics")){
                    electronicsCategory = categories.get(i).categoryId();
                } else if (categories.get(i).categoryName().equals("Home Appliances")){
                    homeAppliancesCategory = categories.get(i).categoryId();
                }
            }
            log.info("MANIPULATING DATA NOW!");

            List<Product> products = webClientBuilder.build()
                    .get()
                    .uri("http://product-management-service/api/v1/product-management/get-all?pageNumber=" + 0 + "&pageSize=" + 10 + "&sort=lastUpdated")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntityList(Product.class)
                    .block()
                    .getBody();

            String electronicsResponse = "In Electronics we have";
            String homeAppliancesResponse = "In Home Appliances we have";

            for (int i=0;i<products.size();i++){
                if(products.get(i).category() == electronicsCategory){
                    electronicsResponse = electronicsResponse + ", "+ products.get(i).name();
                } else if(products.get(i).category() == homeAppliancesCategory){
                    homeAppliancesResponse = homeAppliancesResponse + ", " + products.get(i).name();
                }
            }

            electronicsResponse = electronicsResponse + " and other products.";
            homeAppliancesResponse = homeAppliancesResponse + " and other products.";

            for (IntentData intent : intentList) {
                if(intent.tag().equals("items")){
                    intent.responses().clear();
                    intent.responses().add(itemsResponse);
                }
            }

            for (IntentData intent : intentList) {
                if(intent.tag().equals("electronics")){
                    intent.responses().clear();
                    intent.responses().add(electronicsResponse);
                } else if (intent.tag().equals("homeappliances")){
                    intent.responses().clear();
                    intent.responses().add(homeAppliancesResponse);
                }
                System.out.println("Tag: " + intent.tag());
                System.out.println("Patterns: " + intent.patterns());
                System.out.println("Responses: " + intent.responses());
                System.out.println();
            }

            objectMapper.writeValue(new File("C:/Users/shehr/OneDrive/Desktop/chatbot-service/intents.json"), intents);
        } catch (FileNotFoundException fileNotFoundException){
            log.error("ERROR FILE NOT FOUND",fileNotFoundException);
        } catch (JsonParseException jsonParseException){
            log.error("ERROR PARSING JSON DATA FROM FILE",jsonParseException);
        } catch (Exception ex){
            log.error("ERROR",ex);
        }
    }
}