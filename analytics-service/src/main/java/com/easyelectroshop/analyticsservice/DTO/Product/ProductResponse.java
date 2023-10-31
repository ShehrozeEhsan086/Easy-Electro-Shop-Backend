package com.easyelectroshop.analyticsservice.DTO.Product;

import java.util.UUID;

public record ProductResponse(
        UUID productId,
        String productName,
        String coverImage,
        long soldCount
) {
}
