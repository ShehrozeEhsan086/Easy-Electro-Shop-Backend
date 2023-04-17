package com.easyelectroshop.webscrappingservice.Controller;

import com.easyelectroshop.webscrappingservice.Service.WebScrapperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/web-scrapper")
public class WebScrapperController {

    @Autowired
    WebScrapperService webScrapperService;

    @PostMapping("/get-product-price-amazon")
    public ResponseEntity getProductPriceAmazon(@RequestBody String productName){
        String price = webScrapperService.getPriceFromAmazon(productName);
        return ( price != null) ? ResponseEntity.ok(price) : ResponseEntity.notFound().build();
    }

}