package com.easyelectroshop.couponservice.Repository;

import com.easyelectroshop.couponservice.Model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface CouponRepository extends JpaRepository<Coupon,Long> {

//    @Query(value = "SELECT * FROM coupon WHERE coupon_code = ?1 AND customer_id = ?2",nativeQuery = true)
//    Coupon getCouponByCouponCodeAndCustomer(String couponCode, UUID customerId);

    Coupon findCouponByCouponCodeAndCustomerId(String couponCode, UUID customerId);
}
