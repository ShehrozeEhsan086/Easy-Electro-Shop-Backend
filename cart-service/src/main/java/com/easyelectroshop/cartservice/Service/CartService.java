package com.easyelectroshop.cartservice.Service;

import com.easyelectroshop.cartservice.Model.Cart;
import com.easyelectroshop.cartservice.Model.CartContent;
import com.easyelectroshop.cartservice.Repository.CartRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.*;

@Service
@Slf4j
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

                // INCREASING QUANTITY VALUE OF PRODUCT IF IT IS PRESENT IN CART
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
                // REDUCE PRODUCT COUNT IN STOCK HERE!!!!
                return HttpStatusCode.valueOf(200);

            } catch (Exception ex) {
                log.error("ERROR WHILE ADDING PRODUCT TO CART ", ex);
                return HttpStatusCode.valueOf(500);
            }
        } else {
            // Make a new cart object here
            //FOLLOWING IS IN COMPLETE
            try{
                return HttpStatusCode.valueOf(200);
            } catch (Exception ex){
                log.error("ERROR WHILE ADDING PRODUCT TO CART ", ex);
                return HttpStatusCode.valueOf(500);
            }
        }
    }
}