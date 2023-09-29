package com.easyelectroshop.productservice.DTO.DiscountDTO;

import java.util.UUID;

public record DiscountResponse(
        long discountId,
        UUID productId,
        String productName,
        String coverImage,
        double productPrice,
        double discountedPrice,
        int discountPercentage,
        String startsAt,
        String endsAt,
        boolean active
) {
}
