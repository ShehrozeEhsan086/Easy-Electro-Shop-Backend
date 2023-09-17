package com.easyelectroshop.ordertrackingservice.Configuration;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SeleniumConfiguration {

//        Background Task No Edge opening
        @Bean
        public WebDriver getEdgeDriver(){
              return WebDriverManager
              .edgedriver()
              .capabilities(new EdgeOptions().addArguments("--headless","--remote-allow-origins=*"))
              .create();
        }

//    @Bean
//    public WebDriver getEdgeDriver(){
//        return WebDriverManager.edgedriver()
//                .capabilities(new EdgeOptions().addArguments("--remote-allow-origins=*"))
//                .create();
//    }
}