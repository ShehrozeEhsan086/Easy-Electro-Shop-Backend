package com.easyelectroshop.webscrappingservice.Service;

import com.easyelectroshop.webscrappingservice.Model.WebScrapper;
import com.easyelectroshop.webscrappingservice.Repository.WebScrapperRepository;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


import java.text.DecimalFormat;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class WebScrapperService {

    @Autowired
    WebScrapperRepository webScrapperRepository;

    @Autowired
    WebDriver webDriver;

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


    public WebScrapper scrapePriceAmazon(String productName, UUID productId) {
        try{
            log.info("SCRAPPING AMAZON.COM FOR PRODUCT WITH PRODUCT_NAME "+productName);
            WebScrapper tempScrapper = webScrapperRepository.findAllByProductIdAndSite(productId,"Amazon");
            WebScrapper amazonScrapper = getPriceFromAmazon(productName,productId);
            if(tempScrapper == null){
                amazonScrapper.setIsVisible(false);
                webScrapperRepository.save(amazonScrapper);
            } else {
                tempScrapper.setScrappedPrice(amazonScrapper.getScrappedPrice());
                webScrapperRepository.save(tempScrapper);
            }
            log.info("SUCCESSFULLY SCRAPPED AMAZON.COM FOR PRODUCT WITH PRODUCT_NAME "+productName);
            return webScrapperRepository.findAllByProductIdAndSite(productId,"Amazon");
        } catch (Exception ex){
            log.error("ERROR WHILE SCRAPPING PRICES ",ex);
            return null;
        }
    }

    public WebScrapper scrapePriceDaraz(String productName, UUID productId) {
        try{
            log.info("SCRAPPING DARAZ.PK FOR PRODUCT WITH PRODUCT_NAME "+productName);
            WebScrapper tempDarazScrapper = webScrapperRepository.findAllByProductIdAndSite(productId,"Daraz");
            WebScrapper darazScrapper = getPriceFromDaraz(productName,productId);
            if(tempDarazScrapper == null){
                darazScrapper.setIsVisible(false);
                webScrapperRepository.save(darazScrapper);
            } else {
                tempDarazScrapper.setScrappedPrice(darazScrapper.getScrappedPrice());
                webScrapperRepository.save(tempDarazScrapper);
            }
            log.info("SUCCESSFULLY SCRAPPED DARAZ.PK FOR PRODUCT WITH PRODUCT_NAME "+productName);
            return webScrapperRepository.findAllByProductIdAndSite(productId,"Daraz");
        } catch (Exception ex){
            log.error("ERROR WHILE SCRAPPING PRICES ",ex);
            return null;
        }
    }

    public WebScrapper getPriceFromAmazon(String productName, UUID productId){
        WebScrapper webScrapper = new WebScrapper();
        try{
            webDriver.get(amazonUrl);

            WebElement location = webDriver.findElement(By.id("glow-ingress-line2"));
            if(location.getText().equals("Pakistan")){
                WebElement locationButton = webDriver.findElement(By.id("nav-global-location-popover-link"));
                Thread.sleep(1000l);
                locationButton.click();

                Thread.sleep(1000l);
                WebElement selectCountryPostalCode = webDriver.findElement(By.id("GLUXZipUpdateInput"));
                Thread.sleep(1000l);
                selectCountryPostalCode.click();
                Thread.sleep(1000l);
                selectCountryPostalCode.sendKeys("10001");

                Thread.sleep(1000l);
                WebElement applyButton = webDriver.findElement(By.cssSelector("[aria-labelledby=GLUXZipUpdate-announce]"));
                Thread.sleep(1000l);
                applyButton.click();

                Thread.sleep(1000l);
                WebElement closeButton = webDriver.findElement(By.cssSelector(".a-popover-footer #GLUXConfirmClose"));
                Thread.sleep(1000l);
                closeButton.click();

                Thread.sleep(5000l);
            }

            WebElement searchField = webDriver.findElement(By.id("twotabsearchtextbox"));
            searchField.sendKeys(productName);

            WebElement searchButton = webDriver.findElement(By.cssSelector("input[value='Go']"));
            searchButton.click();

            int counter = 1;
            WebElement firstProduct = null;
            boolean checkFlag;
            boolean breakLoop = true;
            do {
                checkFlag = true;
                try{
                    firstProduct = webDriver.findElement(By.cssSelector("[cel_widget_id='MAIN-SEARCH_RESULTS-"+counter+"']"));
                } catch (Exception ex){
                    checkFlag = false;
                }
                if(!checkFlag){
                    breakLoop = true;
                } else {
                    if(!firstProduct.getText().contains("Sponsored") && firstProduct.getText().contains("$")){
                        breakLoop = false;
                    }
                }
                counter++;
            } while(breakLoop);

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


    public WebScrapper getPriceFromDaraz(String productName, UUID productId){
        WebScrapper webScrapper = new WebScrapper();
        try{
            webDriver.get(darazUrl);
            WebElement searchFiled = webDriver.findElement(By.id("q"));
            searchFiled.sendKeys(productName);

            WebElement searchButton = webDriver.findElement(By.className("search-box__button--1oH7"));
            searchButton.click();

            WebElement products = webDriver.findElement(By.className("box--ujueT"));

            String regex = "Rs\\.\\s*(.*?)\\s*\n";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(products.getText());

            if (matcher.find()) {
                String extractedValue = matcher.group(1);
                webScrapper.setProductId(productId);
                webScrapper.setScrappedPrice("PKR "+extractedValue);
                webScrapper.setSite("Daraz");
                return webScrapper;
            } else {
                log.error("ERROR WHILE SCRAPPING PRICE FROM DARAZ!");
                return null;
            }
        } catch (Exception ex){
            log.error("ERROR WHILE SCRAPPING PRICE FROM DARAZ!");
            return null;
        }
    }

    public List<WebScrapper> getScrappedPrices(UUID productId){
        log.info("GETTING ALL SCRAPPED PRICES FOR PRODUCT WITH PRODUCT_ID "+productId);
        try{
            return webScrapperRepository.findAllByProductId(productId);
        } catch (Exception ex){
            log.error("ERROR GETTING SCRAPPED PRICES FOR PRODUCT WITH PRODUCT_ID "+productId);
            return null;
        }
    }

    private String convert_USD_to_PKR(String price) throws Exception{
        log.info("CONVERTING SCRAPPED PRICE " + price+ " TO PKR");
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

                long value = Long.parseLong(amountStr);

                // Create a DecimalFormat object to format the number with commas
                DecimalFormat decimalFormat = new DecimalFormat("#,###");

                // Format the number and store it as a string
                return decimalFormat.format(value);
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
        Double pkrPrice = Double.parseDouble(price) * 300;
        return Double.toString(pkrPrice);
    }


    public ResponseEntity<HttpStatusCode> changeScrappedPriceVisibilityAmazon(UUID productId) {
        log.info("CHANGING VISIBILITY AMAZON VALUE OF SCRAPPED PRICES FOR PRODUCT WITH PRODUCT_ID "+productId);
        try{
            WebScrapper scrappedPrices = webScrapperRepository.findAllByProductIdAndSite(productId,"Amazon");
            if(scrappedPrices == null){
                log.error("NO SCRAPPED PRICES FOR PRODUCT WITH PRODUCT_ID "+productId+" FOUND!");
                return ResponseEntity.status(404).build();
            } else {
                boolean currentValue = scrappedPrices.isVisible();
                boolean newValue;
                if (currentValue){
                    newValue = false;
                } else {
                    newValue = true;
                }
                scrappedPrices.setIsVisible(newValue);
                webScrapperRepository.save(scrappedPrices);
                return ResponseEntity.status(200).build();
            }
        } catch (Exception ex){
            log.error("COULD NOT CHANGE VISIBILITY OF AMAZON VALUE SCRAPPED PRICES FOR PRODUCT WITH PRODUCT_ID "+productId);
            return ResponseEntity.status(500).build();
        }
    }

    public ResponseEntity<HttpStatusCode> changeScrappedPriceVisibilityDaraz(UUID productId) {
        log.info("CHANGING VISIBILITY DARAZ VALUE OF SCRAPPED PRICES FOR PRODUCT WITH PRODUCT_ID "+productId);
        try{
            WebScrapper scrappedPrices = webScrapperRepository.findAllByProductIdAndSite(productId,"Daraz");
            if(scrappedPrices == null){
                log.error("NO SCRAPPED PRICES FOR PRODUCT WITH PRODUCT_ID "+productId+" FOUND!");
                return ResponseEntity.status(404).build();
            } else {
                boolean currentValue = scrappedPrices.isVisible();
                boolean newValue;
                if (currentValue){
                    newValue = false;
                } else {
                    newValue = true;
                }
                scrappedPrices.setIsVisible(newValue);
                webScrapperRepository.save(scrappedPrices);
                return ResponseEntity.status(200).build();
            }
        } catch (Exception ex){
            log.error("COULD NOT CHANGE VISIBILITY OF DARAZ VALUE SCRAPPED PRICES FOR PRODUCT WITH PRODUCT_ID "+productId);
            return ResponseEntity.status(500).build();
        }
    }

    
    public ResponseEntity<HttpStatusCode> changeScrappedPriceVisibility(UUID productId) {
        log.info("CHANGING VISIBILITY VALUE OF SCRAPPED PRICES FOR PRODUCT WITH PRODUCT_ID "+productId);
        try{
            List<WebScrapper> scrappedPrices = webScrapperRepository.findAllByProductId(productId);
            if(scrappedPrices == null){
                log.error("NO SCRAPPED PRICES FOR PRODUCT WITH PRODUCT_ID "+productId+" FOUND!");
                return ResponseEntity.status(404).build();
            } else {
                boolean currentValue = scrappedPrices.get(0).isVisible();
                boolean newValue;
                if (currentValue){
                    newValue = false;
                } else {
                    newValue = true;
                }
                for (int i=0; i<scrappedPrices.size();i++){
                    scrappedPrices.get(i).setIsVisible(newValue);
                }
                webScrapperRepository.saveAll(scrappedPrices);
                return ResponseEntity.status(200).build();
            }
        } catch (Exception ex){
            log.error("COULD NOT CHANGE VISIBILITY OF SCRAPPED PRICES FOR PRODUCT WITH PRODUCT_ID "+productId);
            return ResponseEntity.status(500).build();
        }
    }

    public List<WebScrapper> getAll(){
        return webScrapperRepository.findAll();
    }
}