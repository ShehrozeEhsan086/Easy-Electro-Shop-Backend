package com.easyelectroshop.webscrappingservice.Controller;

import com.easyelectroshop.webscrappingservice.Model.WebScrapper;
import com.easyelectroshop.webscrappingservice.Service.WebScrapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/web-scrapper")
public class WebScrapperController {

    @Autowired
    WebScrapperService webScrapperService;

    @PostMapping("/scrape-product-price-amazon/{productId}/{productName}")
    public ResponseEntity<WebScrapper> scrapeProductPricesAmazon(@PathVariable UUID productId , @PathVariable String productName){
        WebScrapper response = webScrapperService.scrapePriceAmazon(productName,productId);
        return ( response != null) ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }

    @PostMapping("/scrape-product-price-daraz/{productId}/{productName}")
    public ResponseEntity<WebScrapper> scrapeProductPricesDaraz(@PathVariable UUID productId , @PathVariable String productName){
        WebScrapper response = webScrapperService.scrapePriceDaraz(productName,productId);
        return ( response != null) ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }

    @GetMapping("/get-scrapped-prices/{productId}")
    public ResponseEntity<List<WebScrapper>> getScrappedPrices(@PathVariable UUID productId){
        List<WebScrapper> response =  webScrapperService.getScrappedPrices(productId);
        return ( response != null) ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }

    @PutMapping("/change-scrapped-price-visibility/{productId}")
    public ResponseEntity<HttpStatusCode> changeScrappedPriceVisibility(@PathVariable UUID productId){
        return webScrapperService.changeScrappedPriceVisibility(productId);
    }


}