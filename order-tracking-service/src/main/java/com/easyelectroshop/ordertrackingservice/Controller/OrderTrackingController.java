package com.easyelectroshop.ordertrackingservice.Controller;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderTrackingController {

    @Autowired
    WebDriver webDriver;

}
