package com.easyelectroshop.customerservice.Service;

import com.easyelectroshop.customerservice.DTO.Cart.Cart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
public class CartService {

    @Autowired
    WebClient.Builder webClientBuilder;

    public HttpStatusCode addProductToCart(UUID customerId, UUID productId, int quantity){
        log.info("CALLING CART SERVICE TO ADD PRODUCT "+productId+" TO CART OF CUSTOMER "+customerId);
        try{
            return webClientBuilder.build()
                    .post()
                    .uri("http://cart-service/api/v1/cart-service/add-to-cart/"+customerId+"/"+productId+"/"+quantity)
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> Mono.just(response.getStatusCode()))
                    .block();
        }  catch (WebClientResponseException.NotAcceptable notAcceptable){
            log.error("ERROR CALLING CART SERVICE TO ADD PRODUCT "+productId+" TO CART OF CUSTOMER NOT ENOUGH STOCK!"+customerId);
            return HttpStatusCode.valueOf(406);
        }
        catch (Exception ex){
            log.error("ERROR CALLING CART SERVICE TO ADD PRODUCT "+productId+" TO CART OF CUSTOMER "+customerId,ex);
            return HttpStatusCode.valueOf(500);
        }
    }


    public HttpStatusCode removeItem(UUID customerId, UUID productId){
        log.info("CALLING CART SERVICE TO REMOVE PRODUCT "+productId+" FROM CART OF CUSTOMER "+customerId);
        try{
            return webClientBuilder.build()
                    .put()
                    .uri("http://cart-service/api/v1/cart-service/remove-item-from-cart/"+customerId+"/"+productId)
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> Mono.just(response.getStatusCode()))
                    .block();
        } catch (WebClientResponseException.NotFound notFound){
            log.error("A CART FOR CUSTOMER OR ITEM IN CART NOT FOUND");
            return HttpStatusCode.valueOf(404);
        }catch (Exception ex){
            log.error("ERROR CALLING CART SERVICE TO REMOVE PRODUCT "+productId+" FROM CART OF CUSTOMER "+customerId,ex);
            return HttpStatusCode.valueOf(500);
        }
    }

    public HttpStatusCode reduceItem(UUID customerId, UUID productId){
        log.info("CALLING CART SERVICE TO REDUCE PRODUCT "+productId+" FROM CART OF CUSTOMER "+customerId);
        try{
            return webClientBuilder.build()
                    .put()
                    .uri("http://cart-service/api/v1/cart-service/reduce-item-quantity-from-cart/"+customerId+"/"+productId)
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> Mono.just(response.getStatusCode()))
                    .block();
        } catch (WebClientResponseException.NotFound notFound){
            log.error("A CART FOR CUSTOMER OR ITEM IN CART NOT FOUND");
            return HttpStatusCode.valueOf(404);
        }catch (Exception ex){
            log.error("ERROR CALLING CART SERVICE TO REDUCE PRODUCT "+productId+" FROM CART OF CUSTOMER "+customerId,ex);
            return HttpStatusCode.valueOf(500);
        }
    }

    public Cart getCustomerCart(UUID customerId){
        log.info("CALLING CART SERVICE TO GET CART OF CUSTOMER "+customerId);
        try{
            return webClientBuilder.build()
                    .get()
                    .uri("http://cart-service/api/v1/cart-service/get-customer-cart/"+customerId)
                    .retrieve()
                    .bodyToMono(Cart.class)
                    .block();
        } catch (WebClientResponseException.NotFound notFound){
            log.error("CART FOR CUSTOMER NOT FOUND");
            return null;
        }catch (Exception ex){
            log.error("ERROR CALLING CART SERVICE TO GET CART OF CUSTOMER "+customerId,ex);
            return null;
        }
    }

    public HttpStatusCode cancelCart(UUID customerId){
        log.info("CALLING CART SERVICE TO CANCEL CART OF CUSTOMER "+customerId);
        try{
            return webClientBuilder.build()
                    .delete()
                    .uri("http://cart-service/api/v1/cart-service/cancel-customer-cart/"+customerId)
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> Mono.just(response.getStatusCode()))
                    .block();
        } catch (WebClientResponseException.NotFound notFound){
            log.error("A CART FOR CUSTOMER OR ITEM IN CART NOT FOUND");
            return HttpStatusCode.valueOf(404);
        }catch (Exception ex){
            log.error("ERROR CALLING CART SERVICE TO CANCEL CART OF CUSTOMER "+customerId,ex);
            return HttpStatusCode.valueOf(500);
        }
    }

    public HttpStatusCode deleteCart(UUID customerId){
        log.info("CALLING CART SERVICE TO DELETE CART OF CUSTOMER "+customerId);
        try{
            return webClientBuilder.build()
                    .delete()
                    .uri("http://cart-service/api/v1/cart-service/delete-customer-cart/"+customerId)
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> Mono.just(response.getStatusCode()))
                    .block();
        } catch (WebClientResponseException.NotFound notFound){
            log.error("A CART FOR CUSTOMER OR ITEM IN CART NOT FOUND");
            return HttpStatusCode.valueOf(404);
        }catch (Exception ex){
            log.error("ERROR CALLING CART SERVICE TO DELETE CART OF CUSTOMER "+customerId,ex);
            return HttpStatusCode.valueOf(500);
        }
    }


}
