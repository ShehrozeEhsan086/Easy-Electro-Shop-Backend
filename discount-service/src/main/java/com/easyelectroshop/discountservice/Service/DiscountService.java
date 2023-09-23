package com.easyelectroshop.discountservice.Service;

import com.easyelectroshop.discountservice.Model.Discount;
import com.easyelectroshop.discountservice.Repository.DiscountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
                if(discount.getStartsAt().isAfter(LocalDate.now())){
                    discount.setActive(false);
                }
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
        log.info("GETTING ACTIVE DISCOUNT FOR ePRODUCT WITH PRODUCT_ID "+productId);
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

    @Scheduled(fixedRate = 900000)
//    @Scheduled(fixedRate = 5000)
    private void activationValidationScheduler(){
        log.info("DISCOUNT ACTIVATION SCHEDULER IS BEING CALLED");
        try{
            log.info("GETTING ALL ACTIVE DISCOUNTS");
            List<Discount> activeDiscounts = discountRepository.getAllActiveDiscounts();
            log.info("SUCCESSFULLY RETRIEVED ALL ACTIVE DISCOUNTS, CURRENT ACTIVE DISCOUNTS COUNT IS "+activeDiscounts.size());
            log.info("SENDING ACTIVE DISCOUNTS TO VALIDATION PROCESSOR");
            validateCurrentlyActiveDiscounts(activeDiscounts);
            log.info("ACTIVE DISCOUNTS VALIDATION COMPLETE PROCESS RETURNED TO SCHEDULER");
            log.info("GETTING ALL NON ACTIVE FUTURE DISCOUNTS");
            List<Discount> nonActiveFutureDiscounts = discountRepository.getAllFutureNonActiveDiscounts();
            log.info("SUCCESSFULLY RETRIEVED ALL NON ACTIVE FUTURE DISCOUNTS, CURRENT COUNT IS "+nonActiveFutureDiscounts.size());
            log.info("SENDING ALL NON ACTIVE FUTURE DISCOUNTS TO VALIDATION PROCESSOR");
            validateNonActiveFutureDiscounts(nonActiveFutureDiscounts);
            log.info("NON ACTIVE FUTURE DISCOUNTS VALIDATION COMPLETE PROCESS RETURNED TO SCHEDULER");
            log.info("SCHEDULING PROCESS SUCCESSFULLY COMPLETE");
        } catch (Exception ex){
            log.error("FATAL ERROR IN SCHEDULER!",ex);
        }
    }

    private void validateCurrentlyActiveDiscounts(List<Discount> activeDiscounts){
        log.info("PROCESSING ACTIVE DISCOUNTS");
        LocalDate currentDate = LocalDate.now();
        try{
            for(int i=0;i<activeDiscounts.size();i++){
                if(activeDiscounts.get(i).getEndsAt().isBefore(currentDate)){
                    log.info("FOUND EXPIRED DISCOUNT!");
                    log.info("DEACTIVATING DISCOUNT WITH DISCOUNT_ID "+activeDiscounts.get(i).getDiscountId());
                    activeDiscounts.get(i).setActive(false);
                }
            }
            discountRepository.saveAll(activeDiscounts);
            log.info("ALL ACTIVE DISCOUNTS PROCESSED AND VALIDATED");
        } catch (Exception ex){
            log.error("FATAL ERROR IN WHILE PROCESSING ACTIVE DISCOUNTS!",ex);
        }
    }

    private void validateNonActiveFutureDiscounts(List<Discount> nonActiveFutureDiscounts){
        log.info("PROCESSING NON-ACTIVE FUTURE DISCOUNTS");
        try{
            for(int i=0;i<nonActiveFutureDiscounts.size();i++){
                if(nonActiveFutureDiscounts.get(i).getStartsAt().isEqual(LocalDate.now()) || nonActiveFutureDiscounts.get(i).getStartsAt().isBefore(LocalDate.now())){
                    log.info("FOUND DORMANT DISCOUNT");
                    log.info("ACTIVATING DISCOUNT WITH DISCOUNT_ID "+nonActiveFutureDiscounts.get(i).getDiscountId());
                    nonActiveFutureDiscounts.get(i).setActive(true);
                }
            }
            discountRepository.saveAll(nonActiveFutureDiscounts);
            log.info("ALL NON ACTIVE FUTURE DISCOUNTS PROCESSED AND VALIDATED");
        }  catch (Exception ex){
            log.error("FATAL ERROR IN WHILE PROCESSING ACTIVE DISCOUNTS!",ex);
        }
    }

}