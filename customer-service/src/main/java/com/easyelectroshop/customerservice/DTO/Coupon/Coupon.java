package com.easyelectroshop.customerservice.DTO.Coupon;

import java.time.LocalDate;
import java.util.UUID;

public record Coupon(
        long couponId,
        String couponCode,
        UUID customerId,
        int discountPercentage,
        String validUpto
) {
}
