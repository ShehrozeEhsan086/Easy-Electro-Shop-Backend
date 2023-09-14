package com.easyelectroshop.ordertrackingservice.Service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class OrderTrackingService {

    @Autowired
    WebDriver webDriver;


    @Value("https://www.leopardscourier.com/leopards-tracking")
    private String leopardCourier;

    public String getTrackingInfo(String trackingNumber){
        webDriver.get(leopardCourier);

        WebElement searchField = webDriver.findElement(By.className("track_field"));
        searchField.sendKeys(trackingNumber);


        WebElement searchButton = webDriver.findElement(By.className("submit_button"));
        searchButton.click();

        WebElement result = webDriver.findElement(By.className("we_font_size"));

        return "";
    }


}
