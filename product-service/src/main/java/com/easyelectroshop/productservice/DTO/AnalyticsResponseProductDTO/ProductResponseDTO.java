package com.easyelectroshop.productservice.DTO.AnalyticsResponseProductDTO;

import java.util.UUID;

public record ProductResponseDTO(
        UUID productId,
        String productName,
        String coverImage,
        long soldCount
) {
}
