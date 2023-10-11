package com.easyelectroshop.customerservice.Service;

import com.easyelectroshop.customerservice.DTO.Coupon.Coupon;
import com.easyelectroshop.customerservice.DTO.Coupon.ResponseCoupon;
import com.easyelectroshop.customerservice.DTO.Customer.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class CouponService {


    @Autowired
    WebClient.Builder webClientBuilder;

    public ResponseEntity<HttpStatusCode> addCoupon(Coupon coupon){
        log.info("CALLING COUPON MANAGEMENT SERVICE TO ADD COUPON FOR CUSTOMER WITH CUSTOMER_ID "+coupon.customerId());
        try{
            webClientBuilder.build()
                    .post()
                    .uri("http://coupon-service/api/v1/coupon-service/add-coupon")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(coupon))
                    .retrieve()
                    .toEntity(HttpStatusCode.class)
                    .block();

            webClientBuilder.build()
                    .post()
                    .uri("http://amazon-ses-service/api/v1/amazon-ses-service/send-coupon-email")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(coupon))
                    .retrieve()
                    .toEntity(HttpStatusCode.class)
                    .block();

            return ResponseEntity.status(HttpStatusCode.valueOf(201)).build();

        } catch (Exception ex){
            log.error("ERROR CALLING  COUPON MANAGEMENT SERVICE TO ADD COUPON FOR CUSTOMER WITH CUSTOMER_ID "+coupon.customerId(),ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<Coupon> getCouponById(long couponId){
        log.info("CALLING COUPON SERVICE TO GET COUPON WITH COUPON_ID "+couponId);
        try{
            return webClientBuilder.build()
                    .get()
                    .uri("http://coupon-service/api/v1/coupon-service/get-coupon-by-id/"+couponId)
                    .retrieve()
                    .toEntity(Coupon.class)
                    .block();
        } catch (Exception ex){
            log.error("ERROR CALLING COUPON SERVICE TO GET COUPON WITH COUPON_ID "+couponId,ex);
            return ResponseEntity.internalServerError().build();
        }
    }


    public ResponseEntity<HttpStatusCode> editCoupon(Coupon coupon){
        log.info("CALLING COUPON SERVICE TO EDIT COUPON WITH COUPON_ID "+coupon.couponId());
        try{
            return webClientBuilder.build()
                    .put()
                    .uri("http://coupon-service/api/v1/coupon-service/edit-coupon")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(coupon))
                    .retrieve()
                    .toEntity(HttpStatusCode.class)
                    .block();
        } catch (Exception ex){
            log.error("ERROR CALLING COUPON SERVICE TO EDIT COUPON WITH COUPON_ID "+coupon.couponId(),ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<List<ResponseCoupon>> getAllCoupons(){
        log.info("CALLING COUPON SERVICE TO GET ALL COUPONS");
        try{
            List<Coupon> coupons = webClientBuilder.build()
                    .get()
                    .uri("http://coupon-service/api/v1/coupon-service/get-all-coupons")
                    .retrieve()
                    .toEntityList(Coupon.class)
                    .block()
                    .getBody();

            List<ResponseCoupon> responseCoupons = new ArrayList<>();
            for(int i =0;i<coupons.size();i++){
                Customer customer = webClientBuilder.build()
                        .get()
                        .uri("http://customer-management-service/api/v1/customer-management/get-customer-by-id/"+coupons.get(i).customerId())
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .toEntity(Customer.class)
                        .block()
                        .getBody();
                ResponseCoupon responseCoupon = new ResponseCoupon(coupons.get(i).couponId(),coupons.get(i).couponCode(),
                        coupons.get(i).customerId(),customer.email(),customer.fullName(),
                        coupons.get(i).discountPercentage(),coupons.get(i).validUpto());
                responseCoupons.add(responseCoupon);
            }
            return ResponseEntity.ok(responseCoupons);
        } catch (Exception ex){
            log.error("ERROR CALLING COUPON SERVICE TO GET ALL COUPONS",ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<HttpStatusCode> deleteCoupon(long couponId){
        log.info("CALLING COUPON SERVICE TO DELETE COUPON WITH COUPON_ID "+couponId);
        try{
            return webClientBuilder.build()
                    .delete()
                    .uri("http://coupon-service/api/v1/coupon-service/delete-coupon/"+couponId)
                    .retrieve()
                    .toEntity(HttpStatusCode.class)
                    .block();
        } catch (Exception ex){
            log.error("ERROR CALLING COUPON SERVICE TO DELETE COUPON WITH COUPON_ID "+couponId,ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    public ResponseEntity<Coupon> getCouponByCodeAndCustomerId(String couponCode, UUID customerId){
        log.info("CALLING COUPON SERVICE TO GET COUPON WITH COUPON_CODE "+couponCode+" FOR CUSTOMER WITH CUSTOMER_ID "+customerId);
        try{
            return webClientBuilder.build()
                    .get()
                    .uri("http://coupon-service/api/v1/coupon-service/get-coupon-for-checkout/"+couponCode+"/"+customerId)
                    .retrieve()
                    .toEntity(Coupon.class)
                    .block();
        } catch (Exception ex){
            log.error("CALLING COUPON SERVICE TO GET COUPON WITH COUPON_CODE "+couponCode+" FOR CUSTOMER WITH CUSTOMER_ID "+customerId,ex);
            return ResponseEntity.internalServerError().build();
        }
    }

}
