package com.easyelectroshop.webscrappingservice.Configuration;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SeleniumConfiguration {

    //    Background Task No Chrome opening
    //    @Bean
    //    public WebDriver getChromeDriver(){
    //        return WebDriverManager.chromedriver().capabilities(new ChromeOptions().addArguments("--headless","--remote-allow-origins=*")).create();
    //    }

    @Bean
    public WebDriver getChromeDriver(){
        return WebDriverManager.chromedriver().capabilities(new ChromeOptions().addArguments("--remote-allow-origins=*")).create();
    }


}