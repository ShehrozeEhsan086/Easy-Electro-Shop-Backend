package com.easyelectroshop.productservice.DTO.ProductRecommendationForCustomer;

import java.util.UUID;

public record ProductRecommendationRequestDTO(
        UUID user_id,
        int n_recommendations
) {
}
