package com.easyelectroshop.cartservice.Controller;

import com.easyelectroshop.cartservice.Model.Cart;
import com.easyelectroshop.cartservice.Service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/remove-item-from-cart/{customerId}/{productId}")
    public ResponseEntity<HttpStatusCode> removeItem(@PathVariable UUID customerId,
                                                     @PathVariable UUID productId){
        return cartService.removeItem(customerId,productId);
    }

    @PutMapping("/reduce-item-quantity-from-cart/{customerId}/{productId}")
    public ResponseEntity<HttpStatusCode> reduceItem(@PathVariable UUID customerId,
                                                     @PathVariable UUID productId){
        return cartService.removeItemQuantityOne(customerId,productId);
    }

    @GetMapping("/get-customer-cart/{customerId}")
    public ResponseEntity<Cart> getCustomerCart(@PathVariable UUID customerId){
        return cartService.getCustomerCart(customerId);
    }

    @DeleteMapping("/cancel-customer-cart/{customerId}")
    public ResponseEntity<HttpStatusCode> cancelCustomerCart(@PathVariable UUID customerId){
        return cartService.cancelCart(customerId);
    }

    @DeleteMapping("/delete-customer-cart/{customerId}")
    public ResponseEntity<HttpStatusCode> deleteCustomerCart(@PathVariable UUID customerId){
        return cartService.deleteCart(customerId);
    }


}