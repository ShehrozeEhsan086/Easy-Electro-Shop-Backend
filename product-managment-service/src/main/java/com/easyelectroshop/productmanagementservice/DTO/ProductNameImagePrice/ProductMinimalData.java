package com.easyelectroshop.productmanagementservice.DTO.ProductNameImagePrice;

import java.util.UUID;

public record ProductMinimalData(
        UUID productId,
        String productName,
        String coverImage

) {
}
