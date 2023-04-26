package com.easyelectroshop.webscrappingservice.Service;

import com.easyelectroshop.webscrappingservice.Model.WebScrapper;
import com.easyelectroshop.webscrappingservice.Repository.WebScrapperRepository;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class WebScrapperService {

    @Autowired
    WebScrapperRepository webScrapperRepository;

    @Autowired
    WebDriver chromeDriver;

    @Autowired
    WebScrapper webScrapper;

    @Autowired
    WebClient.Builder webClientBuilder;

    @Autowired
    List<WebScrapper> webScrapperList;

    @Value("${amazon-url}")
    private String amazonUrl;

    @Value("${daraz-url}")
    private String darazUrl;

    @Value("${currency-exchange-access-key}")
    private String apiKey;


    public List<WebScrapper> scrapePrices(String productName, UUID productId) {
        webScrapperList.clear();
        try{
            log.info("SCRAPPING AMAZON FOR PRODUCT WITH PRODUCT_NAME "+productName);
            WebScrapper tempScrapper = webScrapperRepository.findByProductIdAndSite(productId,"Amazon");
            WebScrapper amazonScrapper = getPriceFromAmazon(productName,productId);
            if(tempScrapper == null){
               webScrapperRepository.save(amazonScrapper);
            } else {
                tempScrapper.setScrappedPrice(amazonScrapper.getScrappedPrice());
                webScrapperRepository.save(tempScrapper);
            }
            webScrapperList.add(amazonScrapper);
            log.info("SUCCESSFULLY SCRAPPED AMAZON FOR PRODUCT WITH PRODUCT_NAME "+productName);


            return  webScrapperList;
        } catch (Exception ex){
            log.error("ERROR WHILE SCRAPPING PRICES ",ex);
            return null;
        }
    }

    public WebScrapper getPriceFromAmazon(String productName, UUID productId){
        try{
            chromeDriver.get(amazonUrl);
            WebElement searchField = chromeDriver.findElement(By.id("twotabsearchtextbox"));
            searchField.sendKeys(productName);

            WebElement searchButton = chromeDriver.findElement(By.cssSelector("input[value='Go']"));
            searchButton.click();

            int counter = 1;
            WebElement firstProduct = null;
            boolean checkFlag;
            boolean breakLoop = true;
            do {
                checkFlag = true;
                try{
                    firstProduct = chromeDriver.findElement(By.cssSelector("[cel_widget_id='MAIN-SEARCH_RESULTS-"+counter+"']"));
                } catch (Exception ex){
                    checkFlag = false;
                    counter ++;
                }
                counter++;
                if(!checkFlag){
                    breakLoop = true;
                } else {
                    if(!firstProduct.getText().contains("Sponsored") && firstProduct.getText().contains("$")){
                        breakLoop = false;
                    }
                }
            } while(breakLoop);

            System.out.println(firstProduct.getText());

            Pattern pattern = Pattern.compile("\\$([0-9,]+(\\.[0-9]*)?)");

            Matcher matcher = pattern.matcher(firstProduct.getText());

            if (matcher.find()){
                String price = matcher.group(1);
                String usdPrice = price.replaceAll(",", "");
                try{
                    webScrapper.setScrappedPrice("PKR "+convert_USD_to_PKR(usdPrice));
                } catch (Exception ex){
                    log.warn("LIVE PRICE CONVERTER API UNAVAILABLE FALLING BACK TO HARD CODED CONVERTER (CONVERSION VALUE 280)");
                    webScrapper.setScrappedPrice("PKR "+convert_USD_to_PKR_Stub(usdPrice));
                }
            } else {
                log.error("ERROR WHILE EXTRACTING PRICE!");
                throw new Exception();
            }
        } catch (Exception ex){
            log.error("ERROR WHILE SCRAPPING PRICE FROM AMAZON!",ex);
            return null;
        }
        webScrapper.setProductId(productId);
        webScrapper.setSite("Amazon");
        return webScrapper;
    }


//    public WebScrapper getPriceFromDaraz(String productName, UUID productId){
//        chromeDriver.get(darazUrl);
//        WebElement searchFiled = chromeDriver.findElement(By.id("a2a0e.home.search.i0.35e34937FNzIxh"));
//        return null;
//    }



    private String convert_USD_to_PKR(String price) throws Exception{
        try{
            String response = WebClient.builder()
                    .baseUrl("https://api.apilayer.com/exchangerates_data/convert?to=PKR&from=USD&amount="+price)
                    .defaultHeader("apiKey",apiKey)
                    .build()
                    .get()
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();

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

    private String convert_USD_to_PKR_Stub(String price){
        Double pkrPrice = Double.parseDouble(price) * 280;
        return Double.toString(pkrPrice);
    }


}