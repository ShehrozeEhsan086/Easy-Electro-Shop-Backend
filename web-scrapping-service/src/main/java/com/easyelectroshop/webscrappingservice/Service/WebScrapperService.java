package com.easyelectroshop.webscrappingservice.Service;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class WebScrapperService {

    @Autowired
    WebDriver chromeDriver;

    @Value("${amazon-url}")
    private String amazonUrl;

    @Value("${daraz-url}")
    private String darazUrl;

    @Value("${currency-exchange-access-key}")
    private String apiKey;

    @Autowired
    WebClient.Builder webClientBuilder;

    public String getPriceFromAmazon(String productName){
        try{
            chromeDriver.get(amazonUrl);
            WebElement searchField = chromeDriver.findElement(By.id("twotabsearchtextbox"));
            searchField.sendKeys(productName);

            WebElement searchButton = chromeDriver.findElement(By.cssSelector("input[value='Go']"));
            searchButton.click();

            int counter = 1;
            WebElement firstProduct;
            do {
                firstProduct = chromeDriver.findElement(By.cssSelector("[cel_widget_id='MAIN-SEARCH_RESULTS-"+counter+"']"));
                counter++;
            } while(firstProduct.getText().contains("Sponsored"));

            Pattern pattern = Pattern.compile("\\$([0-9,]+(\\.[0-9]*)?)");

            Matcher matcher = pattern.matcher(firstProduct.getText());

            if (matcher.find()){
                String price = matcher.group(1);
                String usdPrice = price.replaceAll(",", "");
                return "PKR "+convert_USD_to_PKR(usdPrice);
            } else {
                log.error("Error While Extracting Price!");
                throw new Exception();
            }
        } catch (Exception ex){
            log.error("Error While Scraping Product Price from Amazon!",ex);
            return null;
        }

    }

    private String convert_USD_to_PKR(String price) throws Exception{
        try{
            String response = WebClient.builder()
                    .baseUrl("https://api.apilayer.com/exchangerates_data/convert?to=PKR&from=USD&amount="+price)
                    .defaultHeader("apiKey",apiKey)
                    .build()
                    .get()
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            //Testing Data
            //String response = "{\"success\": true,\n\"query\": {\n\"from\": \"USD\",\n\"to\": \"PKR\",\n\"amount\": 2465\n},\n\"info\": {\n\"timestamp\": 1681694043,\n\"rate\": 283.032512\n},\n\"date\": \"2023-04-17\",\n\"result\": 697675.14208}";

            String responseAmount = "\"result\":\\s*(\\d+)";
            Pattern pattern = Pattern.compile(responseAmount);
            Matcher matcher = pattern.matcher(response);
            if (matcher.find()) {
                String amountStr = matcher.group(1);
                return amountStr;
            } else {
                log.error("Amount not found!");
                throw new Exception();
            }
        } catch (Exception e){
            log.error("Error while converting price from USD to PKR",e);
            throw new Exception();
        }
    }

//    private String convert_USD_to_PKR_Stub(String price){
//        Double pkrPrice = Double.parseDouble(price) * 279.72;
//        return Double.toString(pkrPrice);
//    }
}