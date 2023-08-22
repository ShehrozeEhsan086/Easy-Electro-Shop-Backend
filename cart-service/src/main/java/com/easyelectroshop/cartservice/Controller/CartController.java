package com.easyelectroshop.cartservice.Controller;

import com.easyelectroshop.cartservice.Service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
@RequestMapping("/api/v1/cart-service")
public class CartController {

    @Autowired
    CartService cartService;


    @PostMapping("/add-to-cart/{customerId}/{productId}/{quantity}")
    public ResponseEntity<HttpStatusCode> addProductToCart(@PathVariable UUID customerId,
                                                           @PathVariable UUID productId,
                                                           @PathVariable int quantity){
        return ResponseEntity.status(cartService.addToCart(customerId,productId,quantity)).build();
    }
}