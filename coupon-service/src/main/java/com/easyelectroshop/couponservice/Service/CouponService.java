package com.easyelectroshop.couponservice.Service;

import com.easyelectroshop.couponservice.Model.Coupon;
import com.easyelectroshop.couponservice.Repository.CouponRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CouponService {

    @Autowired
    CouponRepository couponRepository;

    public ResponseEntity<HttpStatusCode> addCoupon(Coupon coupon){
        log.info("ADDING NEW COUPON FOR CUSTOMER WITH CUSTOMER_ID "+coupon.getCustomerId());
        try{
            couponRepository.save(coupon);
            log.info("SUCCESSFULLY ADDED NEW COUPON FOR CUSTOMER WITH CUSTOMER_ID "+coupon.getCustomerId());
            return ResponseEntity.status(HttpStatusCode.valueOf(201)).build();
        } catch (Exception ex){
            log.error("ERROR ADDING NEW COUPON FOR CUSTOMER WITH CUSTOMER_ID "+coupon.getCustomerId(),ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<Coupon> getCouponById(long couponId){
        log.info("GETTING COUPON WITH COUPON_ID "+couponId);
        try{
            return ResponseEntity.ok(couponRepository.findById(couponId).get());
        } catch (Exception ex){
            log.error("ERROR GETTING COUPON WITH COUPON_ID "+couponId,ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<HttpStatusCode> editCoupon(Coupon coupon){
        log.info("EDITING COUPON WITH COUPON_ID "+coupon.getCouponId());
        try{
            couponRepository.save(coupon);
            return ResponseEntity.ok().build();
        } catch (Exception ex){
            log.error("ERROR EDITING COUPON WITH COUPON_ID "+coupon.getCouponId());
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<List<Coupon>> getAllCoupons(){
        log.info("GETTING ALL COUPONS");
        try{
            return ResponseEntity.ok(couponRepository.findAll());
        } catch (Exception ex){
            log.error("ERROR GETTING ALL COUPONS");
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<HttpStatusCode> deleteCoupon(long couponId){
        log.info("DELETING COUPON WITH COUPON_ID "+couponId);
        try{
            couponRepository.deleteById(couponId);
            return ResponseEntity.ok().build();
        } catch (Exception ex){
            log.error("ERROR DELETING COUPON WITH COUPON_ID "+couponId,ex);
            return ResponseEntity.internalServerError().build();
        }
    }



}