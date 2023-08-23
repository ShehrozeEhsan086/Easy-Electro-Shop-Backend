package com.easyelectroshop.cartservice.Service;

import com.easyelectroshop.cartservice.Model.Cart;
import com.easyelectroshop.cartservice.Model.CartContent;
import com.easyelectroshop.cartservice.Repository.CartRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import java.util.*;

@Service
@Slf4j
@Transactional
public class CartService {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    WebClient.Builder webClientBuilder;

    public HttpStatusCode addToCart(UUID customerId, UUID productId, int quantity) {
        log.info("ADDING PRODUCT WITH PRODUCT_ID "+productId+" TO CART OF CUSTOMER WITH CUSTOMER_ID "+customerId+" WITH QUANTITY OF "+quantity);
        Optional<Cart> cart = cartRepository.findByCustomerId(customerId);
        if(cart.isPresent()) {
            try {
                Double productRetrievedPrice = webClientBuilder.build()
                        .get()
                        .uri("http://product-service/api/v1/product/get-price-by-id/" + productId)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(Double.class)
                        .block();

                List<CartContent> currentContent = cart.get().getCartContent();

                boolean productWasAlreadyPresentInCart = false;
                try{
                    webClientBuilder.build()
                            .put()
                            .uri("http://product-service/api/v1/product/reduce-product-stock/"+productId+"/"+quantity)
                            .retrieve()
                            .toBodilessEntity()
                            .flatMap(response -> Mono.just(response.getStatusCode()))
                            .block();

                    for (CartContent cartContent : currentContent) {
                        if (cartContent.getProductId().equals(productId)) {
                            int productQuantity = cartContent.getQuantity();
                            productQuantity += quantity;
                            cartContent.setQuantity(productQuantity);
                            productWasAlreadyPresentInCart = true;
                        } else {
                            continue;
                        }
                    }
                    if (productWasAlreadyPresentInCart){
                        double cartTotalPrice = cart.get().getTotalPrice();
                        cartTotalPrice = cartTotalPrice + (productRetrievedPrice * quantity);
                        cart.get().setTotalPrice(cartTotalPrice);
                    } else {
                        CartContent newContent = new CartContent();
                        newContent.setProductId(productId);
                        newContent.setQuantity(quantity);
                        double cartTotalPrice = cart.get().getTotalPrice();
                        cartTotalPrice = cartTotalPrice + (productRetrievedPrice * quantity);
                        cart.get().setTotalPrice(cartTotalPrice);
                        currentContent.add(newContent);
                        cart.get().setCartContent(currentContent);
                    }

                    cartRepository.save(cart.get());
                    return HttpStatusCode.valueOf(200);

                } catch (Exception ex){
                    log.error("ERROR WHILE ADDING PRODUCT TO CART REJECTED BY PRODUCT SERVICE", ex);
                    return HttpStatusCode.valueOf(500);
                }
            } catch (Exception ex) {
                log.error("ERROR WHILE ADDING PRODUCT TO CART ", ex);
                return HttpStatusCode.valueOf(500);
            }
        } else {
            Cart newCart = new Cart();
            newCart.setCustomerId(customerId);

            CartContent cartContent = new CartContent();
            cartContent.setProductId(productId);
            cartContent.setQuantity(quantity);

            newCart.setCartContent(List.of(cartContent));

            Double productRetrievedPrice = webClientBuilder.build()
                    .get()
                    .uri("http://product-service/api/v1/product/get-price-by-id/" + productId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Double.class)
                    .block();

            newCart.setTotalPrice(productRetrievedPrice * quantity);

            webClientBuilder.build()
                    .put()
                    .uri("http://product-service/api/v1/product/reduce-product-stock/"+productId+"/"+quantity)
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> Mono.just(response.getStatusCode()))
                    .block();

            cartRepository.save(newCart);

            try{
                return HttpStatusCode.valueOf(200);
            } catch (Exception ex){
                log.error("ERROR WHILE ADDING PRODUCT TO CART ", ex);
                return HttpStatusCode.valueOf(500);
            }
        }
    }

    public ResponseEntity<HttpStatusCode> removeItem(UUID customerId, UUID productId){
        log.info("REMOVING ITEM WITH PRODUCT_ID "+productId+" FROM CART OF CUSTOMER WITH CUSTOMER_ID "+customerId);
        try{
            Optional<Cart> cart = cartRepository.findByCustomerId(customerId);
            if (cart.isPresent()){
                List<CartContent> currentContent = cart.get().getCartContent();
                for (CartContent cartContent : currentContent) {
                    if (cartContent.getProductId().equals(productId)) {

                        Double productRetrievedPrice = webClientBuilder.build()
                                .get()
                                .uri("http://product-service/api/v1/product/get-price-by-id/" + productId)
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .bodyToMono(Double.class)
                                .block();

                        double currentTotalPrice = cart.get().getTotalPrice();
                        int quantity = cartContent.getQuantity();
                        cart.get().setTotalPrice(currentTotalPrice - (productRetrievedPrice * quantity));
                        currentContent.remove(cartContent);

                        webClientBuilder.build()
                                .put()
                                .uri("http://product-service/api/v1/product/increase-product-stock/"+productId+"/"+cartContent.getQuantity())
                                .retrieve()
                                .toBodilessEntity()
                                .flatMap(response -> Mono.just(response.getStatusCode()))
                                .block();


                        cartRepository.save(cart.get());

                        if(cart.get().getCartContent().size() == 0){
                            deleteCart(customerId);
                        }

                        return ResponseEntity.ok().build();
                    } else {
                        continue;
                    }
                }
                log.error("PRODUCT WITH PRODUCT_ID "+productId+" NOT FOUND IN CART FOR CUSTOMER WITH CUSTOMER_ID "+customerId);
                return ResponseEntity.notFound().build();
            } else {
                log.error("CART NOT FOUND FOR CUSTOMER WITH CUSTOMER_ID "+customerId);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex){
            log.error("ERROR REMOVING ITEM WITH PRODUCT_ID "+productId+" FROM CART OF CUSTOMER WITH CUSTOMER_ID "+customerId,ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<HttpStatusCode> removeItemQuantityOne(UUID customerId, UUID productId){
        log.info("REMOVING ONE QUANTITY FROM ITEM WITH PRODUCT_ID "+productId+" FROM CART OF CUSTOMER WITH CUSTOMER_ID "+customerId);
        try{
            Optional<Cart> cart = cartRepository.findByCustomerId(customerId);
            if (cart.isPresent()){
                List<CartContent> currentContent = cart.get().getCartContent();
                for (CartContent cartContent : currentContent) {
                    if (cartContent.getProductId().equals(productId)) {

                        Double productRetrievedPrice = webClientBuilder.build()
                                .get()
                                .uri("http://product-service/api/v1/product/get-price-by-id/" + productId)
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .bodyToMono(Double.class)
                                .block();

                        double currentTotalPrice = cart.get().getTotalPrice();

                        int quantity = cartContent.getQuantity();

                        cart.get().setTotalPrice(currentTotalPrice - productRetrievedPrice);

                        if(cartContent.getQuantity() - 1 == 0){
                            currentContent.remove(cartContent);
                        } else {
                            cartContent.setQuantity( quantity - 1 );
                            cart.get().setCartContent(currentContent);
                        }

                        webClientBuilder.build()
                                .put()
                                .uri("http://product-service/api/v1/product/increase-product-stock/"+productId+"/"+1)
                                .retrieve()
                                .toBodilessEntity()
                                .flatMap(response -> Mono.just(response.getStatusCode()))
                                .block();


                        cartRepository.save(cart.get());

                        if(cart.get().getCartContent().size() == 0){
                            deleteCart(customerId);
                        }
                        return ResponseEntity.ok().build();
                    } else {
                        continue;
                    }
                }
                log.error("PRODUCT WITH PRODUCT_ID "+productId+" NOT FOUND IN CART FOR CUSTOMER WITH CUSTOMER_ID "+customerId);
                return ResponseEntity.notFound().build();
            } else {
                log.error("CART NOT FOUND FOR CUSTOMER WITH CUSTOMER_ID "+customerId);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex){
            log.error("ERROR REMOVING ONE QUANTITY FROM ITEM WITH PRODUCT_ID "+productId+" FROM CART OF CUSTOMER WITH CUSTOMER_ID "+customerId,ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<Cart> getCustomerCart(UUID customerId){
        log.info("GETTING CUSTOMER CART FOR CUSTOMER WITH CUSTOMER_ID "+customerId);
        try{
            Optional<Cart> cart = cartRepository.findByCustomerId(customerId);
            if (cart.isPresent()){
                return ResponseEntity.ok(cart.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex){
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<HttpStatusCode> cancelCart(UUID customerId){
        log.info("CANCELLING CART FOR CUSTOMER WITH CUSTOMER_ID "+customerId);
        try{
            Optional<Cart> cart = cartRepository.findByCustomerId(customerId);
            if (cart.isPresent()){
                for (CartContent cartContent : cart.get().getCartContent()) {
                    webClientBuilder.build()
                            .put()
                            .uri("http://product-service/api/v1/product/increase-product-stock/"+cartContent.getProductId()+"/"+cartContent.getQuantity())
                            .retrieve()
                            .toBodilessEntity()
                            .flatMap(response -> Mono.just(response.getStatusCode()))
                            .block();
                }
                cartRepository.delete(cart.get());
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex){
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<HttpStatusCode> deleteCart(UUID customerId){
        log.info("DELETING CART FOR CUSTOMER WITH CUSTOMER_ID "+customerId);
        try{
            Optional<Cart> cart = cartRepository.findByCustomerId(customerId);
            if (cart.isPresent()){
                cartRepository.delete(cart.get());
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex){
            return ResponseEntity.internalServerError().build();
        }
    }
}