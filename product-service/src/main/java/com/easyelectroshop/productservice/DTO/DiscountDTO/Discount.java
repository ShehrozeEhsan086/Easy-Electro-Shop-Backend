package com.easyelectroshop.productservice.DTO.DiscountDTO;

import java.util.UUID;

public record Discount(
        long discountId,
        UUID productId,
        String startsAt,
        String endsAt,
        int discountPercentage,
        boolean active
) {
}
