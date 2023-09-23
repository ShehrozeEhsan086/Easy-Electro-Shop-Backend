package com.easyelectroshop.productservice.DTO.Discount;

import java.time.LocalDate;
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
