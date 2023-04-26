package com.easyelectroshop.webscrappingservice.Controller;

import com.easyelectroshop.webscrappingservice.Model.WebScrapper;
import com.easyelectroshop.webscrappingservice.Service.WebScrapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/web-scrapper")
public class WebScrapperController {

    @Autowired
    WebScrapperService webScrapperService;

    @PostMapping("/scrape-product-prices")
    public ResponseEntity<List<WebScrapper>> scrapeProductPrices(@RequestParam UUID productId , @RequestParam String productName){
        List<WebScrapper> response = webScrapperService.scrapePrices(productName,productId);
        return ( response != null) ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }

}