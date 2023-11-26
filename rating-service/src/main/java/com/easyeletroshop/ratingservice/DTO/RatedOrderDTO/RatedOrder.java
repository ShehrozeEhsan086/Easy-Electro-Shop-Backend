package com.easyeletroshop.ratingservice.DTO.RatedOrderDTO;

import java.util.UUID;

public record RatedOrder(
        UUID customerId,
        long orderId,
        int ratingValue
) {
}
