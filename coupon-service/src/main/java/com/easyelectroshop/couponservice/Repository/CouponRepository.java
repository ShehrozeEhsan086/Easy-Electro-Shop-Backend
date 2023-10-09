package com.easyelectroshop.couponservice.Repository;

import com.easyelectroshop.couponservice.Model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon,Long> {



}
