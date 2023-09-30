package com.easyelectroshop.productservice.Service;

import com.easyelectroshop.productservice.DTO.DiscountDTO.Discount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
@Slf4j
public class DiscountService {

    @Autowired
    WebClient.Builder webClientBuilder;

    public ResponseEntity<HttpStatusCode> addDiscount(Discount discount){
        log.info("CALLING DISCOUNT SERVICE TO ADD DISCOUNT FOR PRODUCT WITH PRODUCT_ID "+discount.productId());
        try{
            webClientBuilder.build()
                    .post()
                    .uri("http://discount-service/api/v1/discount/add-new-discount")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(discount))
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> Mono.just(response.getStatusCode()))
                    .block();
            return ResponseEntity.status(HttpStatusCode.valueOf(201)).build();
        } catch (WebClientResponseException.Conflict conflict){
            log.error("AN ACTIVE DISCOUNT EXISTS FOR PRODUCT WITH PRODUCT_ID "+discount.productId());
            return ResponseEntity.status(HttpStatusCode.valueOf(409)).build();
        } catch (Exception ex){
            log.error("ERROR CALLING DISCOUNT SERVICE TO ADD DISCOUNT FOR PRODUCT WITH PRODUCT_ID "+discount.productId(),ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<HttpStatusCode> activateDiscount(long discountId){
        log.info("CALLING DISCOUNT SERVICE TO ACTIVATE DISCOUNT WITH DISCOUNT_ID "+discountId);
        try{
            webClientBuilder.build()
                    .put()
                    .uri("http://discount-service/api/v1/discount/activate-discount/"+discountId)
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> Mono.just(response.getStatusCode()))
                    .block();
            return ResponseEntity.ok().build();
        } catch (WebClientResponseException.Conflict conflict){
            log.error("CANNOT ACTIVATE DISCOUNT! PRODUCT ALREADY HAS A DISCOUNT ACTIVATED AGAINST IT");
            return ResponseEntity.status(HttpStatusCode.valueOf(409)).build();
        } catch (WebClientResponseException.NotAcceptable notAcceptable){
            log.error("CANNOT ACTIVATE A DISCOUNT THAT EXPIRES IN THE PAST");
            return ResponseEntity.status(HttpStatusCode.valueOf(406)).build();
        } catch (WebClientResponseException.NotFound notFound){
            log.error("DISCOUNT NOT FOUND FOR DISCOUNT_ID "+discountId);
            return ResponseEntity.notFound().build();
        } catch (Exception ex){
            log.error("ERROR CALLING DISCOUNT SERVICE TO ACTIVATE DISCOUNT WITH DISCOUNT_ID "+discountId,ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<HttpStatusCode> deactivateDiscount(long discountId){
        log.info("CALLING DISCOUNT SERVICE TO DE-ACTIVATE DISCOUNT WITH DISCOUNT_ID "+discountId);
        try{
            webClientBuilder.build()
                    .put()
                    .uri("http://discount-service/api/v1/discount/deactivate-discount/"+discountId)
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> Mono.just(response.getStatusCode()))
                    .block();
            return ResponseEntity.ok().build();
        } catch (WebClientResponseException.NotFound notFound){
            log.error("DISCOUNT NOT FOUND FOR DISCOUNT_ID "+discountId);
            return ResponseEntity.notFound().build();
        } catch (Exception ex){
            log.error("ERROR CALLING DISCOUNT SERVICE TO DE-ACTIVATE DISCOUNT WITH DISCOUNT_ID "+discountId,ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<HttpStatusCode> editDiscount(Discount discount){
        log.info("CALLING DISCOUNT SERVICE TO EDIT DISCOUNT WITH DISCOUNT_ID "+discount.discountId());
        try{
            webClientBuilder.build()
                    .put()
                    .uri("http://discount-service/api/v1/discount/edit-discount")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(discount))
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> Mono.just(response.getStatusCode()))
                    .block();
            return ResponseEntity.ok().build();
        } catch (WebClientResponseException.NotFound notFound){
            log.error("DISCOUNT NOT FOUND FOR DISCOUNT_ID "+discount.discountId());
            return ResponseEntity.notFound().build();
        } catch (Exception ex){
            log.error("ERROR CALLING DISCOUNT SERVICE TO EDIT DISCOUNT WITH DISCOUNT_ID "+discount.discountId(),ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<List<Discount>> getAll(){
        log.info("CALLING DISCOUNT SERVICE TO GET ALL DISCOUNTS");
        try{
            return webClientBuilder.build()
                    .get()
                    .uri("http://discount-service/api/v1/discount/get-all")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntityList(Discount.class)
                    .block();
        } catch (Exception ex){
            log.error("ERROR CALLING DISCOUNT SERVICE TO GET ALL DISCOUNTS",ex);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    public ResponseEntity<Discount> getById(long discountId){
        log.info("CALLING DISCOUNT SERVICE TO GET DISCOUNT WITH DISCOUNT_ID "+discountId);
        try{
            return webClientBuilder.build()
                    .get()
                    .uri("http://discount-service/api/v1/discount/get-by-id/"+discountId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(Discount.class)
                    .block();
        } catch (WebClientResponseException.NotFound notFound){
            log.error("DISCOUNT NOT FOUND FOR DISCOUNT_ID "+discountId);
            return ResponseEntity.notFound().build();
        } catch (Exception ex){
            log.error("ERROR CALLING DISCOUNT SERVICE TO GET DISCOUNT WITH DISCOUNT_ID "+discountId,ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<List<Discount>> getAllByProductId(UUID productId){
        log.info("CALLING DISCOUNT SERVICE TO GET ALL DISCOUNTS FOR PRODUCT WITH PRODUCT_ID "+productId);
        try{
            return webClientBuilder.build()
                    .get()
                    .uri("http://discount-service/api/v1/discount/get-all-by-product-id/"+productId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntityList(Discount.class)
                    .block();
        } catch (Exception ex){
            log.error("ERROR CALLING DISCOUNT SERVICE TO GET ALL DISCOUNTS FOR PRODUCT WITH PRODUCT_ID "+productId,ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<Discount> getActiveByProductId(UUID productId){
        log.info("CALLING DISCOUNT SERVICE TO GET ACTIVE DISCOUNT FOR PRODUCT WITH PRODUCT_ID "+productId);
        try{
            return webClientBuilder.build()
                    .get()
                    .uri("http://discount-service/api/v1/discount/get-active-by-product-id/"+productId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(Discount.class)
                    .block();
        } catch (WebClientResponseException.NotFound notFound){
            log.error("COULD NOT FIND ANY ACTIVE DISCOUNT FOR PRODUCT WITH PRODUCT_ID "+productId);
            return ResponseEntity.notFound().build();
        } catch (Exception ex){
            log.error("ERROR CALLING DISCOUNT SERVICE TO GET ACTIVE DISCOUNT FOR PRODUCT WITH PRODUCT_ID "+productId);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<HttpStatusCode> deleteById(long discountId){
        log.info("CALLING DISCOUNT SERVICE TO DELETE DISCOUNT WITH DISCOUNT_ID "+discountId);
        try{
            webClientBuilder.build()
                    .delete()
                    .uri("http://discount-service/api/v1/discount/delete-by-id/"+discountId)
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> Mono.just(response.getStatusCode()))
                    .block();
            return ResponseEntity.ok().build();
        } catch (Exception ex){
            log.error("ERROR CALLING DISCOUNT SERVICE TO DELETE DISCOUNT WITH DISCOUNT_ID "+discountId);
            return ResponseEntity.internalServerError().build();
        }
    }
    
}