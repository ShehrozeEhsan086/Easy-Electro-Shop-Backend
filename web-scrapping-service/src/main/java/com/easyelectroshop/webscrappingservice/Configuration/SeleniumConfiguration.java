package com.easyelectroshop.webscrappingservice.Configuration;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SeleniumConfiguration {

//        Background Task No Chrome opening
//        @Bean
//        public WebDriver getChromeDriver(){
//              return WebDriverManager.edgedriver().capabilities(new EdgeOptions().addArguments("--headless",,"--remote-allow-origins=*")).create();
//        }

    @Bean
    public WebDriver getChromeDriver(){
        return WebDriverManager.edgedriver().capabilities(new EdgeOptions().addArguments("--remote-allow-origins=*")).create();
    }


}