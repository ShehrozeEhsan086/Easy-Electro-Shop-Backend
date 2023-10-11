package com.easyelectroshop.customerservice.DTO.Coupon;

import java.util.UUID;

public record ResponseCoupon(long couponId,
                             String couponCode,
                             UUID customerId,

                             String email,
                             String fullName,
                             int discountPercentage,
                             String validUpto) {
}
