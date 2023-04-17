package com.easyelectroshop.webscrappingservice.Service;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
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
    private String accessKey;

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
                return convert_USD_to_PKR_Stub(usdPrice);
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
            String response = webClientBuilder.build()
                    .get()
                    .uri("https://api.apilayer.com/exchangerates_data/convert?to=PKR&from=&amount="+price)
                    .header("access-key", accessKey)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            String exchangeRate = response.split(":")[2].replaceAll("[^0-9\\.]", "");
            Double pkrPrice = Double.parseDouble(price) * Double.parseDouble(exchangeRate);
            return Double.toString(pkrPrice);
        } catch (Exception e){
            log.error("Error while converting price from USD to PKR",e);
            throw new Exception(e);
        }
    }

    private String convert_USD_to_PKR_Stub(String price){
        Double pkrPrice = Double.parseDouble(price) * 279.72;
        return Double.toString(pkrPrice);
    }
}