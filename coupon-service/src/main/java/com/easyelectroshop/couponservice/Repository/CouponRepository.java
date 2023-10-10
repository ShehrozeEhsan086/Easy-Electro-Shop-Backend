package com.easyelectroshop.couponservice.Repository;

import com.easyelectroshop.couponservice.Model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CouponRepository extends JpaRepository<Coupon,Long> {

    Coupon getCouponByCouponCodeAndCustomerId(String couponCode, UUID customerId);

}
