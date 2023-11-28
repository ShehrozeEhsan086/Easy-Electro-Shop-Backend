package com.easyelectroshop.customerservice.DTO.RatedOrderDTO;

import java.util.UUID;

public record RatedOrder(
        UUID customerId,
        long orderId,
        int ratingValue
) {
}
