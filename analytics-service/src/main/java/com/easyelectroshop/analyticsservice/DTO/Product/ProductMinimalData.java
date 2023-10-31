package com.easyelectroshop.analyticsservice.DTO.Product;

import java.util.UUID;

public record ProductMinimalData(
        UUID productId,
        String productName,
        String coverImage

) {
}
