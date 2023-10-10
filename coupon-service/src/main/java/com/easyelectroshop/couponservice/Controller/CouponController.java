package com.easyelectroshop.couponservice.Controller;

import com.easyelectroshop.couponservice.Model.Coupon;
import com.easyelectroshop.couponservice.Service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/coupon-service")
public class CouponController {

    @Autowired
    CouponService couponService;

    @PostMapping("/add-coupon")
    public ResponseEntity<HttpStatusCode> addCoupon(@RequestBody Coupon coupon){
        return couponService.addCoupon(coupon);
    }

    @GetMapping("/get-coupon-by-id/{couponId}")
    public ResponseEntity<Coupon> getCouponById(@PathVariable long couponId){
        return couponService.getCouponById(couponId);
    }

    @GetMapping("/get-coupon-for-checkout/{couponCode}/{customerId}")
    public ResponseEntity<Coupon> getCouponByCodeAndCustomerId(@PathVariable String couponCode,
                                                               @PathVariable UUID customerId){
        return couponService.getCouponByCodeAndCustomerId(couponCode,customerId);
    }

    @PutMapping("/edit-coupon")
    public ResponseEntity<HttpStatusCode> editCoupon(@RequestBody Coupon coupon){
        return couponService.editCoupon(coupon);
    }

    @GetMapping("/get-all-coupons")
    public ResponseEntity<List<Coupon>> getAll(){
        return couponService.getAllCoupons();
    }

    @DeleteMapping("/delete-coupon/{couponId}")
    public ResponseEntity<HttpStatusCode> deleteCoupon(@PathVariable long couponId){
        return couponService.deleteCoupon(couponId);
    }

}