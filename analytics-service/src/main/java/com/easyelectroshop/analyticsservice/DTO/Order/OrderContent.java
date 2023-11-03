package com.easyelectroshop.analyticsservice.DTO.Order;

import java.util.UUID;

public record OrderContent(
        long orderContentId,

        UUID productId,

        int quantity
) {
}
