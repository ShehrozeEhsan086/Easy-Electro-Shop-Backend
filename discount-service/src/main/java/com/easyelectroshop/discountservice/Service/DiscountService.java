package com.easyelectroshop.discountservice.Service;

import com.easyelectroshop.discountservice.Model.Discount;
import com.easyelectroshop.discountservice.Repository.DiscountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class DiscountService {

    @Autowired
    DiscountRepository discountRepository;

    public ResponseEntity<HttpStatusCode> addDiscount(Discount discount){
        log.info("ADDING NEW DISCOUNT FOR PRODUCT WITH PRODUCT_ID "+discount.getProductId());
        try{
            Optional<Discount> tempDiscount = discountRepository.getActiveByProductId(discount.getProductId());
            if(tempDiscount.isPresent()){
                log.error("AN ACTIVE DISCOUNT EXISTS FOR PRODUCT WITH PRODUCT_ID "+discount.getProductId());
                return ResponseEntity.status(HttpStatusCode.valueOf(409)).build();
            } else {
                discountRepository.save(discount);
                log.info("SUCCESSFULLY SAVED DISCOUNT FOR PRODUCT WITH PRODUCT_ID "+discount.getProductId());
                return ResponseEntity.status(HttpStatusCode.valueOf(201)).build();
            }
        } catch (Exception ex){
            log.error("ERROR ADDING NEW DISCOUNT FOR PRODUCT WITH PRODUCT_ID "+discount.getDiscountId(),ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<HttpStatusCode> editDiscount(Discount discount){
        log.info("EDITING INFO FOR DISCOUNT WITH DISCOUNT_ID "+discount.getDiscountId());
        try{
            Optional<Discount> tempDiscount = discountRepository.findById(discount.getDiscountId());
            if (tempDiscount.isPresent()){
                discountRepository.save(discount);
                log.info("SUCCESSFULLY EDITED INFO FOR DISCOUNT WITH DISCOUNT_ID "+discount.getDiscountId());
                return ResponseEntity.ok().build();
            } else {
                log.error("COULD NOT FIND INFO FOR DISCOUNT WITH DISCOUNT_ID "+discount.getDiscountId());
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex){
            log.error("ERROR EDITING INFO FOR DISCOUNT WITH DISCOUNT_ID "+discount.getDiscountId(),ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<List<Discount>> getAll(){
        log.info("GETTING ALL DISCOUNTS");
        try{
            return ResponseEntity.ok(discountRepository.findAll());
        } catch (Exception ex){
            log.error("ERROR GETTING ALL DISCOUNTS",ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<HttpStatusCode> deleteAll(){
        log.info("DELETING ALL DISCOUNTS");
        try{
            discountRepository.deleteAll();
            return ResponseEntity.ok().build();
        } catch (Exception ex){
            log.error("ERROR DELETING ALL DISCOUNTS",ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<Discount> getById(long discountId){
        log.info("GETTING DISCOUNT INFO FOR DISCOUNT WITH DISCOUNT_ID "+discountId);
        try{
            Optional<Discount> discount = discountRepository.findById(discountId);
            if (discount.isPresent()){
                log.info("SUCCESSFULLY RETRIEVED INFO FOR DISCOUNT WITH DISCOUNT_ID "+discountId);
                return ResponseEntity.ok(discount.get());
            } else {
                log.error("COULD NOT FIND INFO FOR DISCOUNT WITH DISCOUNT_ID "+discountId);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex){
            log.error("ERROR GETTING DISCOUNT INFO FOR DISCOUNT WITH DISCOUNT_ID "+discountId,ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<List<Discount>> getAllByProductId(UUID productId){
        log.info("GETTING ALL DISCOUNTS FOR PRODUCT WITH PRODUCT_ID "+productId);
        try{
            return ResponseEntity.ok(discountRepository.findAllByProductId(productId));
        } catch (Exception ex){
            log.error("ERROR GETTING ALL DISCOUNTS FOR PRODUCT WITH PRODUCT_ID "+productId,ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<Discount> getActiveByProductId(UUID productId){
        log.info("GETTING ACTIVE DISCOUNT FOR PRODUCT WITH PRODUCT_ID "+productId);
        try{
            Optional<Discount> discount = discountRepository.getActiveByProductId(productId);
            if (discount.isPresent()){
                log.info("SUCCESSFULLY RETRIEVED INFO FOR DISCOUNT WITH DISCOUNT_ID "+discount.get().getDiscountId()+" ACTIVE FOR PRODUCT WITH PRODUCT_ID "+productId);
                return ResponseEntity.ok(discount.get());
            } else {
                log.error("COULD NOT FIND ANY ACTIVE DISCOUNT FOR PRODUCT WITH PRODUCT_ID "+productId);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex){
            log.error("ERROR GETTING ALL DISCOUNTS FOR PRODUCT WITH PRODUCT_ID "+productId,ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<HttpStatusCode> deleteById(long discountId){
        log.info("DELETING DISCOUNT WITH DISCOUNT_ID "+discountId);
        try{
            discountRepository.deleteById(discountId);
            return ResponseEntity.ok().build();
        } catch (Exception ex){
            log.error("ERROR DISCOUNT WITH DISCOUNT_ID "+discountId,ex);
            return ResponseEntity.internalServerError().build();
        }
    }

}