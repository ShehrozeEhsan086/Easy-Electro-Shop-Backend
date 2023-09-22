package com.easyelectroshop.ordertrackingservice.Service;

import org.openqa.selenium.*;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class OrderTrackingService {

    @Autowired
    WebDriver webDriver;


    @Value("${leopard-url}")
    private String leopardCourier;

    public ResponseEntity<String> getTrackingInfo(String trackingNumber){
        try{
            webDriver.get(leopardCourier);

            log.info("SCRAPPING LEOPARD COURIER FOR SHIPMENT WITH TRACKING_NUMBER: "+trackingNumber);

            WebElement searchField = webDriver.findElement(By.className("track_field"));
            searchField.sendKeys(trackingNumber);

            WebElement searchButton = webDriver.findElement(By.className("submit_button"));
            searchButton.click();
            WebElement result;

            try{
                result = webDriver.findElement(By.cssSelector("[class=\"table table-borderless\"]"));
            } catch (Exception ex){
                log.error("DATA NOT FOUND FOR SHIPMENT WITH TRACKING_NUMBER "+trackingNumber);
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(result.getScreenshotAs(OutputType.BASE64));
        } catch (Exception ex){
            log.info("ERROR SCRAPPING LEOPARD COURIER FOR TRACKING_NUMBER: "+trackingNumber);
            return ResponseEntity.internalServerError().build();
        }

    }
}
